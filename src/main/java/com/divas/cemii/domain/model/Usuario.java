package com.divas.cemii.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Table(name = "tb_usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    //@NotBlank(message = "Este campo é obrigatório")
    private String nome;

    //@NotBlank(message = "Este campo é obrigatório")
    private String email;

    //@NotBlank(message = "Este campo é obrigatório")
    private String telefone;

    //@NotBlank(message = "Este campo é obrigatório")
    private String cpf;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] foto;

    //@NotBlank(message = "Este campo é obrigatório")
    private String parentesco;

    //@NotBlank(message = "Este campo é obrigatório")
    private String profissao;

    @ManyToOne
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;

    @Column(name = "senha", length  = 254)
    private String senha;

    //@NotBlank(message = "Este campo é obrigatório")
    @Column(name = "nascimento", columnDefinition = "datetime")
    private LocalDate nascimento;

    private String coren;

    @Column(nullable = false)
    private String tipo;

    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Idoso> idosos;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String diasHorarios;

    private String experiencia;
}
