package com.nucleo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_regra", length = 120, nullable = false)
    private String nomeRegra;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoAlerta tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id")
    private ContasBancarias conta;

    @Column(name = "limite_valor", precision = 14, scale = 2)
    private BigDecimal limiteValor;

    @Column(name = "janela_dias")
    private Integer janelaDias;

    @Column(name = "notificar_email", nullable = false)
    @Builder.Default
    private Boolean notificarEmail = true;

    public enum TipoAlerta {
        LIMITE_CATEGORIA,
        GASTO_ATIPICO,
        SALDO_MINIMO
    }
}
