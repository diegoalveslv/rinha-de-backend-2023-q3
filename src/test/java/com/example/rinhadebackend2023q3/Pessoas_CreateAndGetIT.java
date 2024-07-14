package com.example.rinhadebackend2023q3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.rinhadebackend2023q3.infra.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import({TestcontainersConfigurationPostgres.class, TestcontainersConfigurationRedis.class})
@AutoConfigureMockMvc
public class Pessoas_CreateAndGetIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createAndGetPessoa() throws Exception {
        //warmup
        var request1 = new CreatePessoaRequest("apelido", "nome", LocalDate.now(), new String[]{"java", "node"});

        mockMvc.perform(post("/pessoas").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();

        //real deal
        var request2 = new CreatePessoaRequest("apelido2", "nome", LocalDate.now(), new String[]{"java", "node"});

        var mvcResult = mockMvc.perform(post("/pessoas").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request2)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andReturn();

        String pessoaLocation = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);

        mockMvc.perform(get(pessoaLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value(request2.getApelido()))
                .andExpect(jsonPath("$.nome").value(request2.getNome()))
                .andExpect(jsonPath("$.nascimento").value(request2.getNascimento().toString()))
                .andExpect(jsonPath("$.stack").isArray())
                .andExpect(jsonPath("$.stack").value(hasItems(request2.getStack())));
    }
}
