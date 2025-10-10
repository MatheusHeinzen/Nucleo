package com.nucleo.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @Email(message = "Caso mude o email é necessario realizar login novamente com o email atualizado.")
    private String email;

    private String senha;
    private Boolean ativo = true;
}
