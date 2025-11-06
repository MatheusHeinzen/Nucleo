package com.nucleo.controller;

import com.nucleo.model.Meta;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.service.MetaService;
import com.nucleo.service.UsuarioService;
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
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MetaController {

    private final MetaService metaService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;


    /**
     * Endpoint: POST /metas
     * Cria uma nova meta financeira.
     */
    @PostMapping
    public ResponseEntity<Meta> criar(@RequestBody Meta meta) {
        meta.setUsuarioId(usuarioService.getUsuarioIdLogado());
        Meta novaMeta = metaService.criar(meta);
        return new ResponseEntity<>(novaMeta, HttpStatus.CREATED);
    }

    /**
     * Endpoint: GET /metas
     * Lista todas as metas do usuário logado.
     */
    @GetMapping
    public ResponseEntity<List<Meta>> listar() {
        List<Meta> metas = metaService.listarPorUsuario(usuarioService.getUsuarioIdLogado());
        return ResponseEntity.ok(metas);
    }

//    /**
//     * Endpoint: GET /metas/{id}
//     * Busca uma meta específica pelo seu ID.
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
//        Meta meta = metaService.buscarPorId(id, usuarioService.getUsuarioIdLogado());
//        return ResponseEntity.ok(meta);
//    }

    /**
     * Endpoint: PUT /metas/{id}
     * Atualiza uma meta existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Meta> atualizar(@PathVariable Long id, @RequestBody Meta meta) {
        Meta metaAtualizada = metaService.atualizar(id, meta, usuarioService.getUsuarioIdLogado());
        return ResponseEntity.ok(metaAtualizada);
    }

    /**
     * Endpoint: DELETE /metas/{id}
     * Cancela uma meta (altera seu status para 'cancelada').
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        metaService.cancelar(id, usuarioService.getUsuarioIdLogado());
        return ResponseEntity.noContent().build();
    }

}