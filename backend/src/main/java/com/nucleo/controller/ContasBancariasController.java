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
        ContasBancarias novaConta = contasService.criar(conta);
        return new ResponseEntity<>(novaConta, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarMinhas() {
        List<ContasBancarias> contas = contasService.listarPorUsuario(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().body(contas);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarTodas() {
        List<ContasBancarias> contas = contasService.listarTodas();
        return ResponseEntity.ok().body(contas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ContasBancarias> buscarPorId(@PathVariable Long id) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        ContasBancarias conta = contasService.buscarPorId(id, usuarioId, isAdmin);
        return ResponseEntity.ok().body(conta);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContasBancarias>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ContasBancarias> contas = contasService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok().body(contas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ContasBancarias> atualizar(@PathVariable Long id, @RequestBody ContasBancarias conta) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        ContasBancarias contaAtualizada = contasService.atualizar(id, conta, usuarioId, isAdmin);
        return ResponseEntity.ok().body(contaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        contasService.deletar(id, usuarioId, isAdmin);
        return ResponseEntity.ok().build();
    }
}