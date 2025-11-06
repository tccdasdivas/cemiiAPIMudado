package com.divas.cemii.domain.repository;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    // 🔹 Retorna todas as cidades de um estado, ordenadas pelo nome

    Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);

    List<Cidade> findByEstadoIdOrderByNome(Long estadoId);

    Optional<Cidade> findByNomeAndEstado_SiglaIgnoreCase(String nome, String siglaEstado);

}