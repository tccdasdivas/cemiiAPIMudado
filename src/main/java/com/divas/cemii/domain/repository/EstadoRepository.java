package com.divas.cemii.domain.repository;

import com.divas.cemii.domain.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    Optional<Estado> findByNome(String nome);
    Optional<Estado> findBySigla(String sigla);
}
