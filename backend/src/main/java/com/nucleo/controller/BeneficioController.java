package com.nucleo.controller;

import com.nucleo.model.Beneficio;
import com.nucleo.service.BeneficioService;
import com.nucleo.service.UsuarioService;
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
public class BeneficioController {

    private final BeneficioService beneficioService;
    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Beneficio> criar(@RequestBody Beneficio beneficio) {
        Beneficio novoBeneficio = beneficioService.criarParaUsuarioLogado(beneficio);
        return new ResponseEntity<>(novoBeneficio, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Beneficio>> listarMeusBeneficios() {
        List<Beneficio> beneficios = beneficioService.buscarPorUsuarioLogado();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Beneficio>> listar() {
        List<Beneficio> beneficios = beneficioService.listarTodos();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Beneficio> buscarPorId(@PathVariable Long id) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        Beneficio beneficio = beneficioService.buscarPorIdEUsuario(id, usuarioId);
        return ResponseEntity.ok(beneficio);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Beneficio>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Beneficio> beneficios = beneficioService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Beneficio>> buscarMeusPorTipo(@PathVariable Beneficio.TipoBeneficio tipo) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        List<Beneficio> beneficios = beneficioService.buscarPorUsuarioETipo(usuarioId, tipo);
        return ResponseEntity.ok(beneficios);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Beneficio> atualizar(@PathVariable Long id, @RequestBody Beneficio beneficio) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        Beneficio beneficioAtualizado = beneficioService.atualizarMeu(id, beneficio, usuarioId);
        return ResponseEntity.ok(beneficioAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        beneficioService.deletarMeu(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}

