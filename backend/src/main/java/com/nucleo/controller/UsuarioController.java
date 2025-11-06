package com.nucleo.controller;

import com.nucleo.model.Usuario;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nucleo.security.SecurityUtils.getCurrentUserEmail;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema Nucleo")
@CrossOrigin(origins = "*") // Permite acesso do frontend
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // READ - Listar todos os usuários
    @GetMapping("/All")
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.encontraTodos();
        return ResponseEntity.ok(usuarios);
    }

    // READ - Buscar usuário por Email
    @GetMapping("/me")
    @Operation(summary = "Buscar usuario logado")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail() {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(SecurityUtils.getCurrentUserEmail());
        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(usuario);

    }

    // UPDATE - Atualizar usuário
//    @PreAuthorize("#id == authentication.principal.id or hasRo    le('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um usuário existente")
    public ResponseEntity<Usuario> atualizarUsuario( @Valid @RequestBody Usuario usuarioDetails) {
        Usuario usuario = usuarioService.atualizaUsuario(usuarioDetails);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }


    // DELETE - Deletar usuário
    @DeleteMapping
    @Operation(summary = "Deletar um usuário")
    public ResponseEntity<String> deletarUsuario() {
        usuarioService.deletaUsuario();
        return ResponseEntity.ok().build();

    }


}