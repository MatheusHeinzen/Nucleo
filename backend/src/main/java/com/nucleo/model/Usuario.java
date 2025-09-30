package com.nucleo.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usuarios")
@Data
@Builder // ✅ ESTA ANOTAÇÃO É ESSENCIAL
@AllArgsConstructor
@Schema(description = "Entidade que representa um usuário do sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String nome;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Column(unique = true, nullable = false)
    @Schema(description = "Email do usuário (único)", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "123456")
    private String senha;

    @Schema(description = "Região do usuário para cálculos de custo de vida", example = "Sudeste")
    private String regiao;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    private Boolean ativo = true;

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }


    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}