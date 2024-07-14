package com.example.rinhadebackend2023q3;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

@Import({TestcontainersConfigurationPostgres.class, TestcontainersConfigurationRedis.class})
@Slf4j
@Sql(scripts = "classpath:db-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PessoasControllerTest_FindPessoas {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PessoasService pessoasService;

    @Test
    public void givenNoSearchQuery_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/pessoas")).andExpect(status().isBadRequest());
    }

    @Test
    public void givenSearchQueryFindsAnything_returnOk() throws Exception {
        mockMvc.perform(get("/pessoas?t=teste"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void givenSearchQueryMatchesApelidoValueCaseInsensitive_shouldReturnOkWithMatchedValue() throws Exception {
        var apelido = "apelido";
        var query = "APELI";
        var createdPessoa = pessoasService.createPessoa(new CreatePessoaRequest(apelido, "name", LocalDate.now(), null));
        mockMvc.perform(get("/pessoas?t=" + query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].apelido").value(createdPessoa.getApelido()))
                .andExpect(jsonPath("$[0].nome").value(createdPessoa.getNome()))
                .andExpect(jsonPath("$[0].nascimento").value(createdPessoa.getNascimento().toString()))
                .andExpect(jsonPath("$[0].stack").isEmpty());
    }

    @Test
    public void givenSearchQueryMatchesNomeValueCaseInsensitive_shouldReturnOkWithMatchedValue() throws Exception {
        var nome = "name";
        var query = "ME";
        var createdPessoa = pessoasService.createPessoa(new CreatePessoaRequest("apelido", nome, LocalDate.now(), null));
        Pessoa createdPessoa2 = pessoasService.createPessoa(new CreatePessoaRequest("apelido2", nome.toUpperCase(), LocalDate.now(), null));

        mockMvc.perform(get("/pessoas?t=" + query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value(createdPessoa.getNome()))
                .andExpect(jsonPath("$[1].nome").value(createdPessoa2.getNome()));
    }

    @Test
    public void givenSearchQueryForStackValueCaseInsensitive_shouldReturnOkWithMatchedValue() throws Exception {
        pessoasService.createPessoa(new CreatePessoaRequest("apelido", "nome", LocalDate.now(), new String[]{"java", "node"}));
        var createdPessoa2 = pessoasService.createPessoa(new CreatePessoaRequest("apelido2", "nome", LocalDate.now(), new String[]{"rust"}));

        mockMvc.perform(get("/pessoas?t=UST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value(createdPessoa2.getNome()));
    }

    @Test
    public void givenSearchQueryForNascimentoValue_shouldReturnOkWithMatchedValue() throws Exception {
        pessoasService.createPessoa(new CreatePessoaRequest("apelido", "nome", LocalDate.parse("2024-02-10"), new String[]{"java", "node"}));
        var createdPessoa2 = pessoasService.createPessoa(new CreatePessoaRequest("apelido2", "nome", LocalDate.parse("2023-02-11"), new String[]{"rust"}));

        mockMvc.perform(get("/pessoas?t=2023-02-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value(createdPessoa2.getNome()));


        mockMvc.perform(get("/pessoas?t=02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}