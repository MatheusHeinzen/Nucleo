package com.nucleo.model;

import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@SuperBuilder      // ✅ herda builder para as filhas
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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