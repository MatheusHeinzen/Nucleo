package com.nucleo.service;

import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.ResourceNotFoundException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

    public AuthResponseDTO autenticar(AuthRequestDTO request) throws AuthenticationException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            var usuario = usuarioRepository.findByEmailAndAtivoTrue(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("usuario.not-found"));

            var jwtToken = jwtTokenProvider.generateToken(
                    new User(
                            usuario.getEmail(),
                            "", // pode ser substituído por "" se não quiser carregar hash
                            usuario.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.name()))
                                    .collect(Collectors.toList())
                    )
            );

            return AuthResponseDTO.builder()
                    .token("Bearer " + jwtToken)
                    .email(usuario.getEmail())
                    .roles(usuario.getRoles())
                    .build();

        } catch (AuthenticationException e) {
            throw new ResourceNotFoundException("login.login-failed");
        }
    }

    public AuthResponseDTO registrar(AuthRequestDTO request) throws EntityNotCreatedException {
        try {

            if (usuarioRepository.findByEmailAndAtivoTrue(request.getEmail()).isPresent()) {
                throw new EntityNotCreatedException("register.user_exists");
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(request.getEmail().split("@")[0]);
            novoUsuario.setEmail(request.getEmail());
            novoUsuario.setSenha(passwordEncoder.encode(request.getSenha()));
            novoUsuario.setRoles(Set.of(Usuario.Role.ROLE_USER));
            novoUsuario.setAtivo(true);

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

            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .email(usuarioSalvo.getEmail())
                    .roles(usuarioSalvo.getRoles())
                    .build();

        } catch (Exception e) {
            throw new EntityNotCreatedException("register.user_Creation-failed");
        }
    }
}