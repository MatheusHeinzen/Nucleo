package com.nucleo.controller;

import com.nucleo.model.Meta;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.MetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Description;
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

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> criar(@RequestBody Meta meta,@PathVariable Long id) {
        if(meta.getId() != null){
            if(SecurityUtils.isAdmin()){
                meta.setId(id);
            }
        }else{
            meta.setUsuarioId(SecurityUtils.getCurrentUserId());

        }
        Meta novaMeta = metaService.criar(meta);
        return new ResponseEntity<>(novaMeta, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Meta>> listarMinhas() {
        List<Meta> metas = metaService.listarPorUsuario(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(metas);
    }

    @Description("buscar todas as metas, apenas para admins")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meta>> listarTodas() {
        List<Meta> metas = metaService.listarTodas();
        return ResponseEntity.ok().body(metas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
        Meta meta = metaService.buscarPorId(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().body(meta);
    }

    @GetMapping("/{id}/usuario/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meta> buscarPorIdComoAdmin(@PathVariable Long id, @PathVariable Long idUsuario) {
        Meta meta = metaService.buscarPorId(id, idUsuario);
        return ResponseEntity.ok().body(meta);
    }



    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meta>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Meta> metas = metaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Meta> atualizar(@PathVariable Long id, @RequestBody Meta meta) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        Meta metaAtualizada = metaService.atualizar(id, meta, usuarioId);
        return ResponseEntity.ok(metaAtualizada);
    }

    @PutMapping("/{id}/usuario/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meta> atualizarComoAdmin(@PathVariable Long id,
                                                   @PathVariable Long idUsuario,
                                                   @RequestBody Meta meta) {
        Meta metaAtualizada = metaService.atualizar(id, meta, idUsuario);
        return ResponseEntity.ok(metaAtualizada);
    }


    @DeleteMapping({"/{id}", "/{id}/{UserId}"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id,@PathVariable(required = false) Long UserId) {
        Long usuarioId = null;
        if(UserId != null){
            if(SecurityUtils.isAdmin()){
                metaService.cancelar(id, UserId);
            }
        }
        metaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

}