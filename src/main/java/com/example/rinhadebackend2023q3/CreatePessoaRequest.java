package com.example.rinhadebackend2023q3;

import com.example.rinhadebackend2023q3.infra.StrictStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePessoaRequest {

    @NotBlank
    @Size(max = 32)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String apelido;

    @NotBlank
    @Size(max = 100)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String nome;

    @NotNull
    private LocalDate nascimento;

    private String[] stack;
}
