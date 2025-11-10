package com.nucleo.controller;

import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.model.Usuario;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema Nucleo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/all")
    @Operation(summary = "Listar todos os usuários para adms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.encontraTodosDTO();
        return ResponseEntity.ok().body(usuarios);

    }

    @GetMapping("/me")
    @Operation(summary = "Buscar usuario logado")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorEmail() {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(usuarioId);
        if(usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(usuario)  ;

    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok().body(usuario);
    }

    @PutMapping({"","/{id}"})
    @Operation(summary = "Atualizar dados do usuário logado ou passando o id se for adm")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario( @RequestBody UsuarioRequestDTO usuarioDetails, @PathVariable(required = false) Long id) {

        if(id != null) {
            if(SecurityUtils.isAdmin()){
                UsuarioResponseDTO usuario =usuarioService.atualizaUsuario(usuarioDetails,id);
                if (usuario == null) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(usuario);
            }
        }

        UsuarioResponseDTO usuario = usuarioService.atualizaUsuario(usuarioDetails);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(usuario);
    }


    @DeleteMapping({ "", "/{id}" })
    @Operation(summary = "Deletar um usuário")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> deletarUsuario(@PathVariable(value = "id", required = false) Long id) {
        if(id!=null) {
            if(SecurityUtils.isAdmin()){
                usuarioService.deletaUsuario(id);
                return ResponseEntity.ok().build();
            }else {
                throw new AuthenticationException("usuario nao pode deletar outro usuario");
            }

        }
        usuarioService.deletaUsuario();
        return ResponseEntity.ok().build();

    }


}