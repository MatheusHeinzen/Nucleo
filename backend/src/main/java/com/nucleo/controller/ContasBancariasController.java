package com.nucleo.controller;

import com.nucleo.model.ContasBancarias;
import com.nucleo.service.ContasBancariasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContasBancariasController {

    @Autowired
    private ContasBancariasService contasService;

    // Método para simular o ID do usuário logado.
    private Long getUsuarioIdLogado() {
        return 1L; // Substituir pela lógica de autenticação real
    }

    /**
     * Endpoint: POST /contas
     * Cria uma nova conta bancária.
     */
    @PostMapping
    public ResponseEntity<ContasBancarias> criar(@RequestBody ContasBancarias conta) {
        conta.setUsuarioId(getUsuarioIdLogado());
        ContasBancarias novaConta = contasService.criar(conta);
        return new ResponseEntity<>(novaConta, HttpStatus.CREATED);
    }

    /**
     * Endpoint: GET /contas
     * Lista todas as contas ativas do usuário logado.
     */
    @GetMapping
    public ResponseEntity<List<ContasBancarias>> listar() {
        List<ContasBancarias> contas = contasService.listarPorUsuario(getUsuarioIdLogado());
        return ResponseEntity.ok(contas);
    }

    /**
     * Endpoint: GET /contas/{id}
     * Busca uma conta ativa específica pelo seu ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContasBancarias> buscarPorId(@PathVariable Long id) {
        ContasBancarias conta = contasService.buscarPorId(id, getUsuarioIdLogado());
        return ResponseEntity.ok(conta);
    }

    /**
     * Endpoint: PUT /contas/{id}
     * Atualiza uma conta existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContasBancarias> atualizar(@PathVariable Long id, @RequestBody ContasBancarias conta) {
        ContasBancarias contaAtualizada = contasService.atualizar(id, conta, getUsuarioIdLogado());
        return ResponseEntity.ok(contaAtualizada);
    }

    /**
     * Endpoint: DELETE /contas/{id}
     * Inativa uma conta (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contasService.deletar(id, getUsuarioIdLogado());
        return ResponseEntity.noContent().build();
    }
}