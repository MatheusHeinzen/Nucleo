package com.nucleo.controller;

import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nucleo.security.SecurityUtils.getCurrentUserEmail;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema Nucleo")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 🔹 Listar todos
    @GetMapping("/all")
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.encontraTodos());
    }

    // 🔹 Buscar usuário logado
    @GetMapping("/me")
    @Operation(summary = "Buscar usuário logado")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioLogado() {
        UsuarioResponseDTO usuario = usuarioService.buscarEuLogado();
        return ResponseEntity.ok(usuario);
    }

    // 🔹 Atualizar usuário logado
    @PutMapping
    @Operation(summary = "Atualizar o usuário logado")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioDetails) {
        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizaUsuario(usuarioDetails);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    // 🔹 Deletar usuário logado
    @DeleteMapping
    @Operation(summary = "Deletar o usuário logado")
    public ResponseEntity<Void> deletarUsuario() {
        usuarioService.deletaUsuario();
        return ResponseEntity.noContent().build();
    }

    // 🔹 Dashboard (mock)
    @GetMapping("/dashboard")
    @Operation(summary = "Obter dados do dashboard do usuário")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorEmail(getCurrentUserEmail());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("usuario", usuario.getNome());
        dashboard.put("isAdmin", usuario.isAdmin());
        dashboard.put("saldoAtual", 2543.75);
        dashboard.put("receitaMes", 3500.00);
        dashboard.put("despesaMes", 956.25);
        dashboard.put("economiaMes", 2543.75);
        dashboard.put("proximasFaturas", List.of("Internet", "Aluguel"));
        dashboard.put("alertas", List.of("Gasto com delivery acima da média"));

        return ResponseEntity.ok(dashboard);
    }
}
