package com.nucleo.controller;

import com.nucleo.dto.TransacaoRequestDTO;
import com.nucleo.dto.TransacaoResponseDTO;
import com.nucleo.model.Transacao;
import com.nucleo.service.TransacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transação", description = "Gerenciamento de usuários do sistema Nucleo")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TransacaoController {

    @Autowired
    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody TransacaoRequestDTO request) {
        Transacao transacao = transacaoService.criar(request);
        return ResponseEntity.ok().body(transacao);
    }
    @PutMapping("/{id}")

    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody TransacaoRequestDTO request) {

        Transacao transacao = transacaoService.atualizar(id, request);
        return ResponseEntity.ok().body(transacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        transacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<TransacaoResponseDTO>> listarPorUsuario() {
        List<Transacao> transacoes = transacaoService.findByUsuarioId();
        List<TransacaoResponseDTO> response = transacoes.stream()
                .map(TransacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/periodo")
    public ResponseEntity<List<Transacao>> buscarPorPeriodo(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim)
    {


            List<Transacao> transacoes = transacaoService.buscarPorPeriodo(dataInicio, dataFim);

            return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}/categoria/{categoriaId}")
    public ResponseEntity<List<TransacaoResponseDTO>> buscarPorCategoria(
            @PathVariable Long usuarioId, @PathVariable Long categoriaId) {

        List<Transacao> transacoes = transacaoService.encontraPorCategoria(categoriaId);
        List<TransacaoResponseDTO> response = transacoes.stream()
                .map(TransacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<TransacaoResponseDTO>> buscarPorTipo(
            @PathVariable Long usuarioId, @PathVariable Transacao.TipoTransacao tipo) {

        List<Transacao> transacoes = transacaoService.encontraPorTipo(tipo);
        List<TransacaoResponseDTO> response = transacoes.stream()
                .map(TransacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/saldo")
    public ResponseEntity<BigDecimal> getSaldo(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(transacaoService.getSaldo(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/resumo")
    public ResponseEntity<String> getResumo(@PathVariable Long usuarioId) {
        BigDecimal entradas = transacaoService.getTotalEntradas(usuarioId);
        BigDecimal saidas = transacaoService.getTotalSaidas(usuarioId);
        BigDecimal saldo = transacaoService.getSaldo(usuarioId);

        String resumo = String.format(
                "Entradas: R$ %.2f | Saídas: R$ %.2f | Saldo: R$ %.2f",
                entradas, saidas, saldo
        );

        return ResponseEntity.ok(resumo);
    }
}