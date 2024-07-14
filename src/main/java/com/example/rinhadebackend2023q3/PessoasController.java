package com.example.rinhadebackend2023q3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PessoasController {

    private final PessoasService pessoasService;

    @PostMapping("/pessoas")
    public ResponseEntity<?> createPessoa(@RequestBody CreatePessoaRequest createPessoaRequest) {
        Pessoa createdPessoa = pessoasService.createPessoa(createPessoaRequest);
        return new ResponseEntity<>(createdPessoa, new LinkedMultiValueMap<>(Map.of(HttpHeaders.LOCATION, List.of("/pessoas/" + createdPessoa.getId()))), HttpStatus.CREATED);
    }

    @GetMapping("/pessoas/{id}")
    public ResponseEntity<?> getPessoa(@PathVariable UUID id) {
//        return ResponseEntity.ok().build();
        Pessoa pessoa = pessoasService.getPessoa(id);

        return ResponseEntity.ok(pessoa);
    }

    @GetMapping("/pessoas")
    public ResponseEntity<?> findPessoas(@RequestParam(name = "t") String searchQuery) {
//        return ResponseEntity.ok().build();
        List<Pessoa> pessoas = pessoasService.findPessoas(searchQuery);

        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/contagem-pessoas")
    public ResponseEntity<?> countPessoas() {
        log.info("finished!");
        return ResponseEntity.ok(pessoasService.countPessoas());
    }
}
