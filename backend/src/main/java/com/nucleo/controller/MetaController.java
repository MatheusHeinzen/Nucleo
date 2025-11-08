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
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}/userid")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id,@PathVariable(required = false) Long idUsuario) {

        Meta meta = metaService.buscarPorId(id, idUsuario);
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meta>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<Meta> metas = metaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}/{idUsuario}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Meta> atualizar(@PathVariable Long id, @RequestBody Meta meta,@PathVariable(required = false) Long idUsuario) {
        Long usuarioId = null;
        if(meta.getId() != null){
            if(SecurityUtils.isAdmin()){
                usuarioId = idUsuario;
            }
        }else{
            usuarioId = SecurityUtils.getCurrentUserId();
        }

        Meta metaAtualizada = metaService.atualizar(id, meta, usuarioId);
        return ResponseEntity.ok(metaAtualizada);
    }

    @DeleteMapping("/{id}/{UserId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id,@PathVariable(required = false) Long UserId) {
        metaService.cancelar(id,UserId);
        return ResponseEntity.noContent().build();
    }

}