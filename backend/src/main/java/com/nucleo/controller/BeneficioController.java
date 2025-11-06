package com.nucleo.controller;

import com.nucleo.model.Beneficio;
import com.nucleo.service.BeneficioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficios")
@Tag(name = "Beneficios", description = "Gerenciamento de Benefícios dos Usuários")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class BeneficioController {

    private final BeneficioService beneficioService;

    @PostMapping
    public ResponseEntity<Beneficio> criar(@RequestBody Beneficio beneficio) {
        Beneficio novoBeneficio = beneficioService.criar(beneficio);
        return new ResponseEntity<>(novoBeneficio, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Beneficio>> listar() {
        List<Beneficio> beneficios = beneficioService.listarTodos();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beneficio> buscarPorId(@PathVariable Long id) {
        Beneficio beneficio = beneficioService.buscarPorId(id);
        return ResponseEntity.ok(beneficio);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Beneficio>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Beneficio> beneficios = beneficioService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Beneficio>> buscarPorTipo(@PathVariable Beneficio.TipoBeneficio tipo) {
        List<Beneficio> beneficios = beneficioService.buscarPorTipo(tipo);
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<Beneficio>> buscarPorUsuarioETipo(
            @PathVariable Long usuarioId,
            @PathVariable Beneficio.TipoBeneficio tipo) {
        List<Beneficio> beneficios = beneficioService.buscarPorUsuarioETipo(usuarioId, tipo);
        return ResponseEntity.ok(beneficios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Beneficio> atualizar(@PathVariable Long id, @RequestBody Beneficio beneficio) {
        Beneficio beneficioAtualizado = beneficioService.atualizar(id, beneficio);
        return ResponseEntity.ok(beneficioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        beneficioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

