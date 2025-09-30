package com.nucleo.model;

import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao extends BaseEntity {

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    // Relacionamento com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public enum TipoTransacao {
        ENTRADA, SAIDA
    }

    public enum Categoria {
        ALIMENTACAO,
        TRANSPORTE,
        MORADIA,
        SAUDE,
        EDUCACAO,
        LAZER,
        SALARIO,
        INVESTIMENTOS,
        OUTROS
    }
}