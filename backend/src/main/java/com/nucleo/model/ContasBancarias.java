package com.nucleo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas_bancarias")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContasBancarias extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String instituicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo;

    @Column(length = 80)
    private String apelido;

    @Column(length = 3)
    @Builder.Default
    private String moeda = "BRL";

    @Column(name = "saldo_inicial", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;
}