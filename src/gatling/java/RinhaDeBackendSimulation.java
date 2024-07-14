import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RinhaDeBackendSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:9999")
            .header("User-Agent", "Agente do Caos - 2023");

    ScenarioBuilder criacaoEConsultaPessoas = scenario("Criação E Talvez Consulta de Pessoas")
            .feed(tsv("pessoas-payloads.tsv").circular())
            .exec(
                    http("criação")
                            .post("/pessoas")
                            .body(StringBody("#{payload}"))
                            .header("content-type", "application/json")
                            // 201 pros casos de sucesso :)
                            // 422 pra requests inválidos :|
                            // 400 pra requests bosta tipo data errada, tipos errados, etc. :(
                            .check(status().in(201, 422, 400))
                            // Se a criacao foi na api1 e esse location request atingir api2, a api2 tem que encontrar o registro.
                            // Pode ser que o request atinga a mesma instancia, mas estatisticamente, pelo menos um request vai atingir a outra.
                            // Isso garante o teste de consistencia de dados
                            .check(status().saveAs("httpStatus"))
                            .checkIf(session -> session.getString("httpStatus").equals("201"))
                            .then(header("Location").saveAs("location"))
            )
            .pause(Duration.ofMillis(1), Duration.ofMillis(30))
            .doIf(session -> session.contains("location"))
            .then(
                    exec(
                            http("consulta")
                                    .get("#{location}")
                    )
            );

    ScenarioBuilder buscaPessoas = scenario("Busca Válida de Pessoas")
            .feed(tsv("termos-busca.tsv").circular())
            .exec(
                    http("busca válida")
                            .get("/pessoas?t=#{t}")
            );

    ScenarioBuilder buscaInvalidaPessoas = scenario("Busca Inválida de Pessoas")
            .exec(
                    http("busca inválida")
                            .get("/pessoas")
                            .check(status().is(400))
            );

    {
        setUp(
                criacaoEConsultaPessoas.injectOpen(
                        constantUsersPerSec(2).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(15)).randomized(),
                        rampUsersPerSec(6).to(600).during(Duration.ofMinutes(3))
                ),
                buscaPessoas.injectOpen(
                        constantUsersPerSec(2).during(Duration.ofSeconds(25)),
                        rampUsersPerSec(6).to(100).during(Duration.ofMinutes(3))
                ),
                buscaInvalidaPessoas.injectOpen(
                        constantUsersPerSec(2).during(Duration.ofSeconds(25)),
                        rampUsersPerSec(6).to(40).during(Duration.ofMinutes(3))
                )
        ).protocols(httpProtocol);
    }
}
