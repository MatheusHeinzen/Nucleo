package com.nucleo.controller;

import com.nucleo.dto.LoginRequest;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.AuthService;
import com.nucleo.service.UsuarioService;
import com.nucleo.utils.EntityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    // READ - Buscar usuário por ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioDetails) {
        Usuario usuario = usuarioService.atualizaUsuario(id,usuarioDetails);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }


    // DELETE - Deletar usuário
    @DeleteMapping
    @Operation(summary = "Deletar um usuário")
    public ResponseEntity<String> deletarUsuario() {
        boolean deletou = usuarioService.deletaUsuario(getCurrentUserEmail());
        if(deletou){
        return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }



    // ENDPOINT ESPECIAL - Dashboard do usuário (simulado)

    @GetMapping("dashboard")
    @Operation(summary = "Obter dados do dashboard do usuário")
    public ResponseEntity<Map<String, Object>> getDashboard() {

        Usuario usuario = usuarioService.buscarUsuarioPorEmail(getCurrentUserEmail());


        // Simulação de dados do dashboard
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("usuario", usuario.getNome());
        dashboard.put("saldoAtual", 2543.75);
        dashboard.put("receitaMes", 3500.00);
        dashboard.put("despesaMes", 956.25);
        dashboard.put("economiaMes", 2543.75);
        dashboard.put("proximasFaturas", List.of("Internet", "Aluguel"));
        dashboard.put("alertas", List.of("Gasto com delivery acima da média"));

        return ResponseEntity.ok(dashboard);
    }


}