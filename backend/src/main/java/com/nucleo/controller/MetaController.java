package com.nucleo.controller;

import com.nucleo.dto.MetaRequestDTO;
import com.nucleo.dto.MetaResponseDTO;
import com.nucleo.service.MetaService;
import com.nucleo.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@Tag(name = "Metas", description = "Gerenciamento de metas financeiras")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<MetaResponseDTO> criar(@RequestBody MetaRequestDTO request) {
        MetaResponseDTO nova = metaService.criar(request);
        return new ResponseEntity<>(nova, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> listar() {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        List<MetaResponseDTO> metas = metaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> atualizar(@PathVariable Long id, @RequestBody MetaRequestDTO request) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        MetaResponseDTO metaAtualizada = metaService.atualizar(id, request, usuarioId);
        return ResponseEntity.ok(metaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        metaService.cancelar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
