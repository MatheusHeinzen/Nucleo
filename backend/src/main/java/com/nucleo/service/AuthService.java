package com.nucleo.service;

import com.nucleo.dto.AuthRequest;
import com.nucleo.dto.AuthResponse;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse autenticar(AuthRequest request) {
        // JÁ IMPLEMENTADO - mas vamos melhorar
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            var usuario = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            var jwtToken = jwtTokenProvider.generateToken(
                    new User(
                            usuario.getEmail(),
                            usuario.getSenha(),
                            usuario.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.name()))
                                    .collect(Collectors.toList())
                    )
            );

            return AuthResponse.builder()
                    .token(jwtToken)
                    .tipo("Bearer")
                    .email(usuario.getEmail())
                    .roles(usuario.getRoles())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Credenciais inválidas");
        }
    }

    public AuthResponse registrar(AuthRequest request) {
        // ✅ AGORA VAMOS IMPLEMENTAR O REGISTRO
        try {
            // Verificar se email já existe
            if (usuarioRepository.findByEmailAndAtivoTrue(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email já cadastrado");
            }

            Usuario novoUsuario = Usuario.builder()
                    .nome(request.getEmail().split("@")[0]) // Nome baseado no email
                    .email(request.getEmail())
                    .senha(passwordEncoder.encode(request.getSenha())) // 🔐 CODIFICAR SENHA
                    .roles(Set.of(Usuario.Role.ROLE_USER)) // Role padrão
                    .ativo(true)
                    .build();

            // Salvar usuário
            Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

            // Gerar token automaticamente
            var jwtToken = jwtTokenProvider.generateToken(
                    new User(
                            usuarioSalvo.getEmail(),
                            usuarioSalvo.getSenha(),
                            usuarioSalvo.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.name()))
                                    .collect(Collectors.toList())
                    )
            );

            return AuthResponse.builder()
                    .token(jwtToken)
                    .tipo("Bearer")
                    .email(usuarioSalvo.getEmail())
                    .roles(usuarioSalvo.getRoles())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar usuário: " + e.getMessage());
        }
    }
}