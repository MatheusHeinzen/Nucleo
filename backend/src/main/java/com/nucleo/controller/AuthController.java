package com.nucleo.controller;

import com.nucleo.dto.AuthRequest;
import com.nucleo.dto.AuthResponse;
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
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.autenticar(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body("Erro no login: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.registrar(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body("Erro no registro: " + e.getMessage());
        }
    }
}