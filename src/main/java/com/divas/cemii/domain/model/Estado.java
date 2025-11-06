package com.divas.cemii.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_estado")
public class Estado {
    @Id
    @EqualsAndHashCode.Include
    private Long id; // id será o código IBGE

    @NotBlank
    private String nome;

    @Column(length = 2)
    private String sigla;
}