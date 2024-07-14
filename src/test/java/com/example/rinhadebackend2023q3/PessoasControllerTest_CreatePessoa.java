package com.example.rinhadebackend2023q3;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static com.example.rinhadebackend2023q3.infra.JsonUtils.asJsonString;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@SpringBootTest
@AutoConfigureMockMvc

@Import({TestcontainersConfigurationPostgres.class, TestcontainersConfigurationRedis.class})
@Slf4j
@Sql(scripts = "classpath:db-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PessoasControllerTest_CreatePessoa {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideInvalidRequest")
    public void givenInvalidRequest_returnUnprocessableEntity(CreatePessoaRequest request) throws Exception {
        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSyntaxRequest")
    public void givenRequestWithInvalidSyntax_thenReturnBadRequest(String requestAsJsonString) throws Exception {
        log.info("request: {}", requestAsJsonString);

        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsJsonString))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidRequest_shouldReturnCreated() throws Exception {
        var request = new CreatePessoaRequest("apelido", "nome", LocalDate.now(), new String[]{"java", "node"});

        mockMvc.perform(post("/pessoas").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    public void givenDuplicatedApelido_thenReturnBadRequest() throws Exception {
        var request = new CreatePessoaRequest("apelido", "nome", LocalDate.now(), new String[]{"java", "node"});

        var mvcResult = mockMvc.perform(post("/pessoas").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();

        String locationValue = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION).replace("/pessoas/", "");
        try {
            UUID.fromString(locationValue);
        } catch (Exception e) {
            fail("not a valid UUID");
        }

        mockMvc.perform(post("/pessoas").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> provideInvalidRequest() {
        var request1 = new CreatePessoaRequest(null, null, null, null);
        var request2 = new CreatePessoaRequest(null, "nome", LocalDate.now(), new String[]{});
        var request3 = new CreatePessoaRequest(randomAlphanumeric(33), "nome", LocalDate.now(), new String[]{});

        var request4 = new CreatePessoaRequest("apelindo", null, LocalDate.now(), new String[]{});
        var request5 = new CreatePessoaRequest("apelindo", "nome", null, new String[]{});
        var request6 = new CreatePessoaRequest("apelindo", randomAlphanumeric(101), LocalDate.now(), new String[]{});

        var request7 = new CreatePessoaRequest("apelindo", "name", LocalDate.now(), new String[]{randomAlphanumeric(33)});

        return Stream.of(
                Arguments.of(request1)
                , Arguments.of(request2)
                , Arguments.of(request3)
                , Arguments.of(request4)
                , Arguments.of(request5)
                , Arguments.of(request6)
                ,
                Arguments.of(request7)
        );
    }

    static Stream<Arguments> provideInvalidSyntaxRequest() {
        var apelidoWithInteger = new ApelidoWithInteger(111, "nome", LocalDate.now(), new String[]{});
        var nomeWithInteger = new NomeWithInteger("apelido", 111, LocalDate.now(), new String[]{});
        var invalidDate = new NascimentoWithString("apelido", "nome", "invalid-date", new String[]{});
        var stackWithObject = new StackWithObject("apelido", "nome", LocalDate.now(), new Object[]{"text", 123, new Object()});

        return Stream.of(Arguments.of(asJsonString(apelidoWithInteger))
                , Arguments.of(asJsonString(nomeWithInteger))
                , Arguments.of(asJsonString(invalidDate))
                , Arguments.of(asJsonString(stackWithObject))
        );
    }

    record ApelidoWithInteger(Integer apelido, String nome, LocalDate nascimento, String[] stack) {
    }

    record NomeWithInteger(String apelido, Integer nome, LocalDate nascimento, String[] stack) {
    }

    record NascimentoWithString(String apelido, String nome, String nascimento, String[] stack) {
    }

    record StackWithObject(String apelido, String nome, LocalDate nascimento, Object[] stack) {
    }
}