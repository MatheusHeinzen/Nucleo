package com.nucleo.model;

import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends BaseEntity {

    private String nome;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles = Set.of(Role.ROLE_USER);

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }
}