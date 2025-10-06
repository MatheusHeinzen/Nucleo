package com.nucleo.model;

import jakarta.persistence.*;
import lombok.Data;

// A anotação @Data do Lombok cria getters, setters, toString, etc. automaticamente.
@Data
@Entity
@Table(name = "categorias", uniqueConstraints = {
        // Garante que a combinação de usuario_id, nome e tipo seja única no banco
        @UniqueConstraint(columnNames = {"usuario_id", "nome", "tipo"})
})
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria tipo;

    @Column(name = "cor_hex", length = 7)
    private String corHex;

    @Column(nullable = false)
    private boolean ativa = true; // Por padrão, uma categoria nova é ativa
}

