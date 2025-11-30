package com.divas.cemii.domain.repository;

import com.divas.cemii.domain.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByUsuarioMandouIdOrUsuarioRecebeuId(Long usuarioMandouId, Long usuarioRecebeuId);

}
