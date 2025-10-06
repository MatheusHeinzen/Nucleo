package com.nucleo.controller;

import com.nucleo.model.Meta;
import com.nucleo.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas")
public class MetaController {

    @Autowired
    private MetaService metaService;

    // Método para simular o ID do usuário logado.
    private Long getUsuarioIdLogado() {
        return 1L; // Substituir pela lógica de autenticação real
    }

    /**
     * Endpoint: POST /metas
     * Cria uma nova meta financeira.
     */
    @PostMapping
    public ResponseEntity<Meta> criar(@RequestBody Meta meta) {
        meta.setUsuarioId(getUsuarioIdLogado());
        Meta novaMeta = metaService.criar(meta);
        return new ResponseEntity<>(novaMeta, HttpStatus.CREATED);
    }

    /**
     * Endpoint: GET /metas
     * Lista todas as metas do usuário logado.
     */
    @GetMapping
    public ResponseEntity<List<Meta>> listar() {
        List<Meta> metas = metaService.listarPorUsuario(getUsuarioIdLogado());
        return ResponseEntity.ok(metas);
    }

    /**
     * Endpoint: GET /metas/{id}
     * Busca uma meta específica pelo seu ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
        Meta meta = metaService.buscarPorId(id, getUsuarioIdLogado());
        return ResponseEntity.ok(meta);
    }



    /**
     * Endpoint: PUT /metas/{id}
     * Atualiza uma meta existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Meta> atualizar(@PathVariable Long id, @RequestBody Meta meta) {
        Meta metaAtualizada = metaService.atualizar(id, meta, getUsuarioIdLogado());
        return ResponseEntity.ok(metaAtualizada);
    }

    /**
     * Endpoint: DELETE /metas/{id}
     * Cancela uma meta (altera seu status para 'cancelada').
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        metaService.cancelar(id, getUsuarioIdLogado());
        return ResponseEntity.noContent().build();
    }
}
