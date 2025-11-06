package com.divas.cemii.dto;

public record ResponseDTO(
        Long id,
        String nome,
        String token,
        String email,
        String tipo
) { }