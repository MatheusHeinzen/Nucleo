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
    private TipoCategoria tipo; // ENTRADA ou SAIDA

    public enum TipoCategoria {
        ENTRADA,
        SAIDA
    }
}