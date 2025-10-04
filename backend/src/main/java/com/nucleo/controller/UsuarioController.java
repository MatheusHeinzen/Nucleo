package com.nucleo.controller;

import com.nucleo.dto.LoginRequest;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.service.AuthService;
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

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema Nucleo")
@CrossOrigin(origins = "*") // Permite acesso do frontend
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CREATE - Criar novo usuário
//    @PostMapping
//    @Operation(summary = "Criar um novo usuário")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
//            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
//    })
//    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
//        try {
//            if (usuarioRepository.findByEmailAndAtivoTrue(usuario.getEmail()).isPresent()) {
//                return ResponseEntity.badRequest().body(null);
//            }
//
//            Usuario novoUsuario = usuarioRepository.save(usuario);
//            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    // READ - Listar todos os usuários
    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
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
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Buscar usuário por Email
    @GetMapping("/me")
    @Operation(summary = "Buscar usuario logado")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Usuario> usuario = usuarioRepository.findByEmailAndAtivoTrue(email);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - Atualizar usuário
//    @PreAuthorize("#id == authentication.principal.id or hasRo    le('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um usuário existente")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioDetails) {


        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();

        if (usuarioDetails.getNome() != null && !usuarioDetails.getNome().isBlank()) {
            usuario.setNome(usuarioDetails.getNome());
        }

        if (usuarioDetails.getEmail() != null && !usuarioDetails.getEmail().isBlank()) {
            usuario.setEmail(usuarioDetails.getEmail());
        }

        if (usuarioDetails.getAtivo() != null) {
            usuario.setAtivo(usuarioDetails.getAtivo());
        }
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioAtualizado);

    }

    // DELETE - Deletar usuário
    @DeleteMapping
    @Operation(summary = "Deletar um usuário")
    public ResponseEntity<String> deletarUsuario() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmailAndAtivoTrue(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        usuarioRepository.delete(usuarioLogado);
        return ResponseEntity.ok().build();
    }

    // ENDPOINT ESPECIAL - Dashboard do usuário (simulado)

    @GetMapping("dashboard")
    @Operation(summary = "Obter dados do dashboard do usuário")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailAndAtivoTrue(email);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Simulação de dados do dashboard
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("usuario", usuarioOptional.get().getNome());
        dashboard.put("saldoAtual", 2543.75);
        dashboard.put("receitaMes", 3500.00);
        dashboard.put("despesaMes", 956.25);
        dashboard.put("economiaMes", 2543.75);
        dashboard.put("proximasFaturas", List.of("Internet", "Aluguel"));
        dashboard.put("alertas", List.of("Gasto com delivery acima da média"));

        return ResponseEntity.ok(dashboard);
    }


    //rota movida para o authController

//    @PostMapping("/login") // Mapeia para POST /api/usuarios/login
//    @Operation(summary = "Fazer login no sistema")
//    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
//
//        // 1. Buscar usuário pelo email
//        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailAndAtivoTrue(loginRequest.getEmail());
//
//        // 2. Verificar se o usuário existe e se a senha confere
//        // (EM PRODUÇÃO, NUNCA armazene a senha em texto puro! Use criptografia!)
//        if (usuarioOptional.isEmpty() || !usuarioOptional.get().getSenha().equals(loginRequest.getSenha())) {
//            // Retorna erro 401 - Unauthorized
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
//        }
//
//        // 3. Se chegou aqui, o login é válido
//        Usuario usuario = usuarioOptional.get();
//        return ResponseEntity.ok("Login bem-sucedido! Bem-vindo, " + usuario.getNome());
//    }
}