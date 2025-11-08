package com.nucleo.dto;

import com.nucleo.model.Usuario;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha,
        Boolean ativo
) {}
