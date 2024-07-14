package com.example.rinhadebackend2023q3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa {

    @Id
    @Column(name = "ID", nullable = false, unique = true, updatable = false)
    @JsonIgnore
    private UUID id;

    @Column(nullable = false, length = 32, unique = true)
    private String apelido;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private LocalDate nascimento;

    @Column(length = 32)
    private List<String> stack;

    @Column(insertable = false, updatable = false)
    private String searchableText;

}
