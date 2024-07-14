package com.example.rinhadebackend2023q3;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PessoasRepository extends JpaRepository<Pessoa, UUID> {

    boolean existsByApelido(String apelido);

    @Query("""
            select p
            from Pessoa p
            where p.searchableText ilike '%' || :searchQuery || '%'
                """)
    List<Pessoa> findBySearchQuery(String searchQuery, Pageable pageable);
}
