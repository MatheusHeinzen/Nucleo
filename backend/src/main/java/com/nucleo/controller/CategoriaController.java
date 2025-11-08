package com.nucleo.controller;

import com.nucleo.dto.CategoriaRequestDTO;
import com.nucleo.dto.CategoriaResponseDTO;
import com.nucleo.model.Categoria;
import com.nucleo.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de Categorias Globais e Pessoais")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> criar(@RequestBody CategoriaRequestDTO dto) {
        Categoria novaCategoria = categoriaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponseDTO.fromEntity(novaCategoria));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarPorUsuario().stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoria));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorTipo(@PathVariable Categoria.TipoCategoria tipo) {
        List<CategoriaResponseDTO> categorias = categoriaService.buscarPorTipo(tipo).stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable Long id, @RequestBody CategoriaRequestDTO dto) {
        Categoria categoriaAtualizada = categoriaService.atualizar(id, dto);
        return ResponseEntity.ok(CategoriaResponseDTO.fromEntity(categoriaAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
