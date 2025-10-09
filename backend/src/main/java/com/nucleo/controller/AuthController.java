package com.nucleo.controller;

import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Gerenciamento de autorizações (login e cadastro).")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.autenticar(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody AuthRequestDTO request) throws EntityNotCreatedException {
        AuthResponseDTO response = authService.registrar(request);
        return ResponseEntity.ok(response);
    }
}