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

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfigurationPostgres.class, TestcontainersConfigurationRedis.class})
@Slf4j
@Sql(scripts = "classpath:db-scripts/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PessoasControllerTest_GetPessoa {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PessoasService pessoasService;

    @Test
    public void givenUuidDoesNotExits_returnNotFound() throws Exception {
        mockMvc.perform(get("/pessoas/12345678-1234-1234-1234-123456789012")).andExpect(status().isNotFound());
    }

    @Test
    public void givenUuidExists_returnOk() throws Exception {
        var createdPessoa = pessoasService.createPessoa(new CreatePessoaRequest("apelido", "name", LocalDate.now(), new String[]{"java", "node"}));
        mockMvc.perform(get("/pessoas/" + createdPessoa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value(createdPessoa.getApelido()))
                .andExpect(jsonPath("$.nome").value(createdPessoa.getNome()))
                .andExpect(jsonPath("$.nascimento").value(createdPessoa.getNascimento().toString()))
                .andExpect(jsonPath("$.stack").isArray())
                .andExpect(jsonPath("$.stack").value(hasItems("java", "node")));
    }

    @Test
    public void givenEmptyStack_returnStackNull() throws Exception {
        var createdPessoa = pessoasService.createPessoa(new CreatePessoaRequest("apelido", "name", LocalDate.now(), null));
        mockMvc.perform(get("/pessoas/" + createdPessoa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value(createdPessoa.getApelido()))
                .andExpect(jsonPath("$.nome").value(createdPessoa.getNome()))
                .andExpect(jsonPath("$.nascimento").value(createdPessoa.getNascimento().toString()))
                .andExpect(jsonPath("$.stack").isEmpty());
    }


}