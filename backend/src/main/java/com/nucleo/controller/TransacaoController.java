package com.nucleo.controller;

import com.nucleo.dto.TransacaoRequest;
import com.nucleo.model.Transacao;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final UsuarioRepository usuarioRepository; // ✅ PRECISAMOS BUSCAR O USUÁRIO

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody TransacaoRequest request) {
        try {
            // Buscar usuário
            var usuario = usuarioRepository.findByIdAndAtivoTrue(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Criar transação
            Transacao transacao = Transacao.builder()
                    .descricao(request.getDescricao())
                    .valor(request.getValor())
                    .data(request.getData())
                    .tipo(request.getTipo())
                    .categoria(request.getCategoria())
                    .usuario(usuario)
                    .build();

            return ResponseEntity.ok(transacaoService.save(transacao));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody TransacaoRequest request) {
        try {
            if (!transacaoService.exists(id)) {
                return ResponseEntity.notFound().build();
            }

            // Buscar usuário
            var usuario = usuarioRepository.findByIdAndAtivoTrue(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Buscar transação existente
            var transacaoExistente = transacaoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

            // Atualizar transação
            transacaoExistente.setDescricao(request.getDescricao());
            transacaoExistente.setValor(request.getValor());
            transacaoExistente.setData(request.getData());
            transacaoExistente.setTipo(request.getTipo());
            transacaoExistente.setCategoria(request.getCategoria());
            transacaoExistente.setUsuario(usuario);

            return ResponseEntity.ok(transacaoService.save(transacaoExistente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!transacaoService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        transacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos específicos
    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<Transacao>> buscarPorPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(transacaoService.findByPeriodo(usuarioId, inicio, fim));
    }

    @GetMapping("/usuario/{usuarioId}/categoria/{categoria}")
    public ResponseEntity<List<Transacao>> buscarPorCategoria(
            @PathVariable Long usuarioId, @PathVariable Transacao.Categoria categoria) {
        return ResponseEntity.ok(transacaoService.findByCategoria(usuarioId, categoria));
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<Transacao>> buscarPorTipo(
            @PathVariable Long usuarioId, @PathVariable Transacao.TipoTransacao tipo) {
        return ResponseEntity.ok(transacaoService.findByTipo(usuarioId, tipo));
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