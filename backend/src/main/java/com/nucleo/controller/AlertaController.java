package com.nucleo.controller;

import com.nucleo.dto.AlertaRequest;
import com.nucleo.dto.AlertaResponse;
import com.nucleo.model.Alerta;
import com.nucleo.service.AlertaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    /**
     * Cria um novo alerta para o usuário informado no header (usuario_id).
     */
    @PostMapping
    public ResponseEntity<AlertaResponse> criar(@RequestHeader("usuario_id") Long usuarioId,
                                                @Valid @RequestBody AlertaRequest request) {
        Alerta alerta = toEntity(request);

        // Escopo garantido pelo header: força o usuarioId do alerta
        alerta.setUsuarioId(usuarioId);

        alertaService.validarAlerta(alerta);

        Alerta salvo = alertaService.save(alerta);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AlertaResponse(salvo));
    }

    /**
     * Lista todos os alertas ATIVOS de um usuário (não paginado).
     */
    @GetMapping
    public ResponseEntity<List<AlertaResponse>> listarPorUsuario(
            @RequestHeader("usuario_id") Long usuarioId) {

        List<AlertaResponse> respostas = alertaService.listarPorUsuario(usuarioId)
                .stream()
                .map(AlertaResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(respostas);
    }

    /**
     * Lista os alertas ATIVOS de um usuário, de forma paginada.
     * Exemplo: GET /api/alertas/paginado?page=0&size=10
     */
    @GetMapping("/paginado")
    public ResponseEntity<Page<AlertaResponse>> listarPorUsuarioPaginado(
            @RequestHeader("usuario_id") Long usuarioId,
            Pageable pageable) {

        Page<Alerta> paginaAlertas = alertaService.listarPorUsuario(usuarioId, pageable);
        Page<AlertaResponse> paginaResposta = paginaAlertas.map(AlertaResponse::new);

        return ResponseEntity.ok(paginaResposta);
    }

    /**
     * Busca um alerta específico pelo id, garantindo que pertence ao usuário do header.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(
            @PathVariable Long id,
            @RequestHeader("usuario_id") Long usuarioId) {

        return alertaService.buscarPorIdEUsuario(id, usuarioId)
                .map(alerta -> ResponseEntity.ok(new AlertaResponse(alerta)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza um alerta específico, garantindo escopo por usuário.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponse> atualizar(@PathVariable Long id,
                                                    @RequestHeader("usuario_id") Long usuarioId,
                                                    @Valid @RequestBody AlertaRequest request) {

        return alertaService.buscarPorIdEUsuario(id, usuarioId)
                .map(existing -> {
                    atualizarEntidade(existing, request);
                    // garante que permanece vinculado ao mesmo usuário do header
                    existing.setUsuarioId(usuarioId);

                    alertaService.validarAlerta(existing);
                    Alerta salvo = alertaService.save(existing);
                    return ResponseEntity.ok(new AlertaResponse(salvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Remove (soft delete) um alerta, garantindo que pertence ao usuário.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @RequestHeader("usuario_id") Long usuarioId) {

        // Verifica existência respeitando escopo do usuário
        if (!alertaService.existePorIdEUsuario(id, usuarioId)) {
            return ResponseEntity.notFound().build();
        }

        // Soft delete via BaseService/BaseRepository
        alertaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================
    // Métodos de mapeamento
    // ==========================

    private Alerta toEntity(AlertaRequest request) {
        Alerta alerta = new Alerta();
        // usuarioId será sobrescrito pelo header no controller
        alerta.setUsuarioId(request.getUsuarioId());
        alerta.setNomeRegra(request.getNomeRegra());
        alerta.setTipo(request.getTipo());
        alerta.setCategoriaId(request.getCategoriaId());
        alerta.setContaId(request.getContaId());
        alerta.setLimiteValor(request.getLimiteValor());
        alerta.setJanelaDias(request.getJanelaDias());

        if (request.getAtivo() != null) {
            alerta.setAtivo(request.getAtivo());
        }
        if (request.getNotificarEmail() != null) {
            alerta.setNotificarEmail(request.getNotificarEmail());
        }

        return alerta;
    }

    private void atualizarEntidade(Alerta alerta, AlertaRequest request) {
        // usuarioId será garantido pelo header no método atualizar()
        alerta.setNomeRegra(request.getNomeRegra());
        alerta.setTipo(request.getTipo());
        alerta.setCategoriaId(request.getCategoriaId());
        alerta.setContaId(request.getContaId());
        alerta.setLimiteValor(request.getLimiteValor());
        alerta.setJanelaDias(request.getJanelaDias());

        if (request.getAtivo() != null) {
            alerta.setAtivo(request.getAtivo());
        }
        if (request.getNotificarEmail() != null) {
            alerta.setNotificarEmail(request.getNotificarEmail());
        }
    }
}
