package com.nucleo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste")
@Tag(name = "Teste", description = "Endpoint simples para testar a API")
public class TesteController {

    @GetMapping
    @Operation(summary = "Retorna uma mensagem de boas-vindas")
    public String mensagemDeTeste() {
        return "✅ API do Nucleo está funcionando perfeitamente!";
    }
}