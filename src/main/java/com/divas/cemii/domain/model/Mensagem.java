package com.divas.cemii.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_mensagem")
public class Mensagem {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    @ManyToOne
    @JoinColumn(name = "usuario_recebeu")
    private Usuario usuarioRecebeu;

    @ManyToOne
    @JoinColumn(name = "usuario_mandou")
    private Usuario usuarioMandou;


    @Column(name = "horarioenvio", columnDefinition = "datetime")
    private LocalDate horarioEnvio;
}
