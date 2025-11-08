package com.nucleo.model;

import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;

    public enum TipoCategoria {
        ENTRADA,
        SAIDA
    }

    @Column(nullable = false)
    @Builder.Default
    private Boolean isGlobal = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}