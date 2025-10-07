package com.nucleo.controller;

import com.nucleo.dto.TransacaoRequest;
import com.nucleo.dto.TransacaoResponse;
import com.nucleo.model.Transacao;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.service.TransacaoService;
import com.nucleo.utils.EntityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.nucleo.utils.EntityUtils;


@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transação", description = "Gerenciamento de usuários do sistema Nucleo")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody TransacaoRequest request) {
        try {
            var usuario = usuarioRepository.findByIdAndAtivoTrue(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            var categoria = categoriaRepository.findByIdAndAtivoTrue(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

            Transacao transacao = Transacao.builder()
                    .descricao(request.getDescricao())
                    .valor(request.getValor())
                    .data(request.getData())
                    .tipo(request.getTipo())
                    .categoria(categoria)
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

            var usuario = usuarioRepository.findByIdAndAtivoTrue(request.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            var categoria = categoriaRepository.findByIdAndAtivoTrue(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

            var transacaoExistente = transacaoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transação não encontrada"));


            EntityUtils.atualizarSeDiferente(transacaoExistente::setDescricao,request.getDescricao(),transacaoExistente.getDescricao());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setValor,request.getValor(),transacaoExistente.getValor());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setData,request.getData(),transacaoExistente.getData());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setTipo,request.getTipo(),transacaoExistente.getTipo());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setCategoria,categoria,transacaoExistente.getCategoria());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setUsuario,usuario,transacaoExistente.getUsuario());


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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Transacao> transacoes = transacaoService.findByUsuarioId(usuarioId);
        List<TransacaoResponse> response = transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<TransacaoResponse>> buscarPorPeriodo(
            @PathVariable Long usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        List<Transacao> transacoes = transacaoService.findByPeriodo(usuarioId, inicio, fim);
        List<TransacaoResponse> response = transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/categoria/{categoriaId}")
    public ResponseEntity<List<TransacaoResponse>> buscarPorCategoria(
            @PathVariable Long usuarioId, @PathVariable Long categoriaId) {

        var categoria = categoriaRepository.findByIdAndAtivoTrue(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        List<Transacao> transacoes = transacaoService.findByCategoria(usuarioId, categoria);
        List<TransacaoResponse> response = transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<TransacaoResponse>> buscarPorTipo(
            @PathVariable Long usuarioId, @PathVariable Transacao.TipoTransacao tipo) {

        List<Transacao> transacoes = transacaoService.findByTipo(usuarioId, tipo);
        List<TransacaoResponse> response = transacoes.stream()
                .map(TransacaoResponse::fromEntity)
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