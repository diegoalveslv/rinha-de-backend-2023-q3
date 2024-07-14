package com.example.rinhadebackend2023q3;

import com.example.rinhadebackend2023q3.infra.BadRequesException;
import com.example.rinhadebackend2023q3.infra.NotFoundException;
import com.example.rinhadebackend2023q3.infra.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class PessoasService {

    private final PessoasRepository pessoasRepository;

    public Pessoa createPessoa(@Valid CreatePessoaRequest request) {
        String[] stack = request.getStack();
        if (stack != null) {
            Stream.of(stack).forEach(v -> {
                if (v.length() > 32) {
                    throw new UnprocessableEntityException();
                }
            });
        }

        var apelido = request.getApelido();
//        if (pessoasRepository.existsByApelido(apelido)) {
//            throw new BadRequesException("apelido %s already exists".formatted(apelido)); 30949
//        }

        var pessoa = Pessoa.builder()
                .id(UUID.randomUUID())
                .apelido(apelido)
                .nome(request.getNome())
                .nascimento(request.getNascimento())
                .stack(stack != null ? Arrays.asList(stack) : null)
                .build();

        return pessoasRepository.save(pessoa);
    }

    public Pessoa getPessoa(UUID id) {
        return pessoasRepository.findById(id).orElseThrow(() -> new NotFoundException("pessoa not found with id=%s".formatted(id)));
    }

    public List<Pessoa> findPessoas(String searchQuery) {
        return pessoasRepository.findBySearchQuery(searchQuery, PageRequest.of(0, 50));
    }

    public Long countPessoas() {
        return pessoasRepository.count();
    }
}
