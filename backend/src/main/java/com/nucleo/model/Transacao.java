package com.nucleo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    public enum Tipo { RECEITA, DESPESA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private BigDecimal valor;
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private String categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}