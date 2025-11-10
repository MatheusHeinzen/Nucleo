package com.nucleo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "beneficios")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficio extends BaseEntity {


    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBeneficio tipo;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public enum TipoBeneficio {
        VR,           // Vale Refeição
        VA,           // Vale Alimentação
        VT,           // Vale Transporte
        GYMPASS,      // Academia
        PLANO_SAUDE,  // Plano de Saúde
        PLANO_ODONTO, // Plano Odontológico
        SEGURO_VIDA,  // Seguro de Vida
        AUXILIO_CRECHE,
        AUXILIO_EDUCACAO,
        OUTRO
    }
}

