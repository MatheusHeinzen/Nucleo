package com.nucleo.controller;

import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Gerenciamento de autorizações (login e cadastro).")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Faz login no sistema",
            description = "Autentica um usuário existente com email e senha.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais do usuário",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Exemplo de login",
                                            value = """
                        {
                          "email": "joao@nucleo.com",
                          "senha": "string"
                        }
                        """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
                    @ApiResponse(responseCode = "400",description = "informacoes invalidas")
            }
    )
    @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.autenticar(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/registrar")
    @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            schema = @Schema(
                    implementation = AuthRequestDTO.class
            ),
            examples = {
                    @ExampleObject(
                            name = "Exemplo de cadastro",
                            value = """
                        {
                          "email": "email@nucleo.com",
                          "senha": "string"
                        }
                        """
                    )
            }
    )))
    public ResponseEntity<?> registrar(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.registrar(request);
        return ResponseEntity.ok(response);
    }
}