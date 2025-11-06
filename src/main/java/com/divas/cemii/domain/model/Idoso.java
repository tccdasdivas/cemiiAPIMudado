package com.divas.cemii.domain.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tb_idoso")
public class Idoso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    private String nome;


    private String cpf;


    private String foto;


    private String necessidade;

    @ManyToOne
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    private String logradouro;

    private String numero;

    @Column(name = "data_nascimento", columnDefinition = "datetime")
    private LocalDate nascimento;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

}
