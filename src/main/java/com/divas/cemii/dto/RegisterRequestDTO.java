package com.divas.cemii.dto;

import com.divas.cemii.domain.model.Cidade;
import com.divas.cemii.domain.model.Usuario;

import java.sql.Blob;
import java.time.LocalDate;

public record RegisterRequestDTO(
        String name,
        String email,
        String password,
        String telefone,
        String cpf,
        LocalDate nascimento,
        String parentesco,
        String profissao,
        CidadeDTO cidade,
        String coren,
        String tipo,
        String experiencia,
        String diasHorarios,
        String foto


) {}
