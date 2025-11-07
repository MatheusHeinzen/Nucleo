package com.nucleo.controller;

import com.nucleo.model.Meta;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.MetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@Tag(name = "Metas", description = "Gerenciamento de metas.")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> criar(@RequestBody Meta meta) {
        meta.setUsuarioId(SecurityUtils.getCurrentUserId());
        Meta novaMeta = metaService.criar(meta);
        return new ResponseEntity<>(novaMeta, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Meta>> listarMinhas() {
        List<Meta> metas = metaService.listarPorUsuario(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(metas);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meta>> listarTodas() {
        List<Meta> metas = metaService.listarTodas();
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        Meta meta = metaService.buscarPorId(id, usuarioId, isAdmin);
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meta>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Meta> metas = metaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> atualizar(@PathVariable Long id, @RequestBody Meta meta) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        Meta metaAtualizada = metaService.atualizar(id, meta, usuarioId, isAdmin);
        return ResponseEntity.ok(metaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();
        metaService.cancelar(id, usuarioId, isAdmin);
        return ResponseEntity.noContent().build();
    }

}