package com.nucleo.service;

import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.ResourceNotFoundException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.JwtTokenProvider;
import com.nucleo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

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

            var jwtToken = jwtTokenProvider.generateToken(UserDetailsImpl.build(usuario));

            return AuthResponseDTO.builder()
                    .token("Bearer " + jwtToken)
                    .email(usuario.getEmail())
                    .build();

        } catch (AuthenticationException e) {
            throw new ResourceNotFoundException("login.login-failed");
        }
    }

    public AuthResponseDTO registrar(AuthRequestDTO request) throws EntityNotCreatedException {
        try {



            System.out.println("entrando no registrar");
            if (usuarioRepository.findByEmailAndAtivoTrue(request.getEmail()).isPresent()) {
                throw new EntityNotCreatedException("register.user_exists");
            }else if(request.getSenha().length() < 6) {}
            System.out.println("passou0");
            Usuario novoUsuario = new Usuario();

            System.out.println("criou usuario");
            novoUsuario.setNome(request.getEmail().split("@")[0]);
            System.out.println("criou novo usuario nome");
            novoUsuario.setEmail(request.getEmail());
            System.out.println("criou novo usuario email");

            novoUsuario.setSenha(passwordEncoder.encode(request.getSenha()));
            System.out.println("criou novo usuario sem=nha");

            novoUsuario.setRoles(Set.of(Usuario.Role.ROLE_USER));
            System.out.println("criou novo usuario role");

            novoUsuario.setAtivo(true);
            System.out.println("criou novo usuario ativo");

            System.out.println("passou");

            Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

            System.out.println("passou 2");
            var jwtToken = jwtTokenProvider.generateToken(UserDetailsImpl.build(usuarioSalvo));

            System.out.println("passou 3");
            return AuthResponseDTO.builder()
                    .token("Bearer " + jwtToken)
                    .email(usuarioSalvo.getEmail())
                    .build();

        } catch (Exception e) {
            throw new EntityNotCreatedException("register.user_Creation-failed");
        }
    }
}