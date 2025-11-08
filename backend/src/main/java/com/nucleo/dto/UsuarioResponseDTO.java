package com.nucleo.dto;

import com.nucleo.model.Usuario;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Set<Usuario.Role> roles,
        Boolean ativo
) {

    public static List<UsuarioResponseDTO> fromEntity (List<Usuario> usuarios) {
        return usuarios.stream().map(UsuarioResponseDTO::fromEntity).collect(Collectors.toList());
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.getAtivo()
        );
    }

}
