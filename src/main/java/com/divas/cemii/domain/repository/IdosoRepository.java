package com.divas.cemii.domain.repository;

import com.divas.cemii.domain.model.Idoso;
import com.divas.cemii.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdosoRepository extends JpaRepository<Idoso, Long> {
}
