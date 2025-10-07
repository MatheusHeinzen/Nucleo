package com.nucleo.controller;

import com.nucleo.model.Categoria;
import com.nucleo.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de Categorias Globais")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // ✅ CATEGORIAS SÃO GLOBAIS - não precisa de usuário logado
    // ❌ REMOVA o método getUsuarioIdLogado()

    @PostMapping
    public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria) {
        // ❌ NÃO seta usuarioId - categoria é global
        Categoria novaCategoria = categoriaService.criar(categoria);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        // ✅ Lista todas as categorias (globais)
        List<Categoria> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Categoria>> buscarPorTipo(@PathVariable Categoria.TipoCategoria tipo) {
        List<Categoria> categorias = categoriaService.buscarPorTipo(tipo);
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaAtualizada = categoriaService.atualizar(id, categoria);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}