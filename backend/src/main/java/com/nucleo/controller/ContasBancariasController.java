package com.nucleo.controller;

import com.nucleo.model.ContasBancarias;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.ContasBancariasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@Tag(name = "Contas Bancárias", description = "Gerenciamento de contas bancárias do usuário")
@RequiredArgsConstructor
public class ContasBancariasController {

    private final ContasBancariasService contasService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ContasBancarias> criar(@RequestBody ContasBancarias conta) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        ContasBancarias novaConta = contasService.criar(conta, usuarioId);
        return new ResponseEntity<>(novaConta, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarMinhas() {
        List<ContasBancarias> contas = contasService.listarPorUsuario(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(contas);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarTodas() {
        List<ContasBancarias> contas = contasService.listarTodas();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ContasBancarias> buscarPorId(@PathVariable Long id) {
        ContasBancarias conta = contasService.buscarPorId(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ContasBancarias> contas = contasService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(contas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ContasBancarias> atualizar(@PathVariable Long id, @RequestBody ContasBancarias conta) {
        ContasBancarias contaAtualizada = contasService.atualizar(id, conta, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(contaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contasService.deletar(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}