package com.nucleo.controller;

import com.nucleo.dto.AlertaRequest;
import com.nucleo.dto.AlertaResponse;
import com.nucleo.model.Alerta;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.AlertaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Gerenciamento de alertas do usuário")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping
    public ResponseEntity<AlertaResponse> criar(@Valid @RequestBody AlertaRequest request) {
        Alerta alerta = alertaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertaResponse.fromEntity(alerta));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AlertaResponse>> listarMeus() {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        List<AlertaResponse> respostas = alertaService.listarPorUsuario(usuarioId)
                .stream()
                .map(AlertaResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respostas);
    }

    @GetMapping("/me/paginado")
    public ResponseEntity<Page<AlertaResponse>> listarMeusPaginado(Pageable pageable) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        Page<Alerta> paginaAlertas = alertaService.listarPorUsuario(usuarioId, pageable);
        Page<AlertaResponse> paginaResposta = paginaAlertas.map(AlertaResponse::fromEntity);
        return ResponseEntity.ok(paginaResposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable Long id) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        return alertaService.buscarPorIdEUsuario(id, usuarioId)
                .map(alerta -> ResponseEntity.ok(AlertaResponse.fromEntity(alerta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/tipo/{tipo}")
    public ResponseEntity<List<AlertaResponse>> buscarMeusPorTipo(@PathVariable Alerta.TipoAlerta tipo) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        List<AlertaResponse> alertas = alertaService.listarAtivosPorTipo(usuarioId, tipo)
                .stream()
                .map(AlertaResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alertas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponse> atualizar(@PathVariable Long id, 
                                                    @Valid @RequestBody AlertaRequest request) {
        Alerta alertaAtualizado = alertaService.atualizar(id, request);
        return ResponseEntity.ok(AlertaResponse.fromEntity(alertaAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alertaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
