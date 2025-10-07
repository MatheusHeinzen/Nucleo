package com.nucleo.controller;

import com.nucleo.model.Meta;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.MetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@Tag(name = "Metas", description = "Gerenciamento de metas.")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtém o ID do usuário logado
     */
    private Long getUsuarioIdLogado() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuario.getId();
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