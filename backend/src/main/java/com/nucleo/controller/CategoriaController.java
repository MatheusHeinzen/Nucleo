package com.nucleo.controller;

import com.nucleo.model.Categoria;
import com.nucleo.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;


    private Long getUsuarioIdLogado() {
        return 1L; // Retorna um ID fixo para fins de desenvolvimento
    }


    @PostMapping
    public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria) {
        categoria.setUsuarioId(getUsuarioIdLogado()); // Associa a categoria ao usuário
        Categoria novaCategoria = categoriaService.criar(categoria);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        List<Categoria> categorias = categoriaService.listarPorUsuario(getUsuarioIdLogado());
        return ResponseEntity.ok(categorias);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id, getUsuarioIdLogado());
        return ResponseEntity.ok(categoria);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaAtualizada = categoriaService.atualizar(id, categoria, getUsuarioIdLogado());
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id, getUsuarioIdLogado());
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
