package com.nucleo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "metas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(name = "valor_alvo", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorAlvo;

    @Column(name = "data_limite", nullable = false)
    private LocalDate dataLimite;
    
    @Column(name = "categoria_id")
    private Long categoriaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusMeta status = StatusMeta.ativa;
}
