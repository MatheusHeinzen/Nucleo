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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema Nucleo")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/All")
    @Operation(summary = "Listar todos os usuários")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.encontraTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar usuario logado")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail() {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(SecurityUtils.getCurrentUserEmail());
        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(usuario);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do usuário logado")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Usuario> atualizarUsuario(@Valid @RequestBody Usuario usuarioDetails) {
        Usuario usuario = usuarioService.atualizaUsuario(usuarioDetails);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }


    @DeleteMapping
    @Operation(summary = "Deletar um usuário")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deletarUsuario() {
        usuarioService.deletaUsuario();
        return ResponseEntity.ok().build();

    }


}