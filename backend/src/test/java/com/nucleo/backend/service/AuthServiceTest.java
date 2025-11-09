package com.nucleo.backend.service;

import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.ResourceNotFoundException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.JwtTokenProvider;
import com.nucleo.security.UserDetailsImpl;
import com.nucleo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioExistente;

    @BeforeEach
    void setup() {
        usuarioExistente = Usuario.builder()
                .id(1L)
                .nome("Isabel")
                .email("isa@nucleo.com")
                .senha("senha123")
                .roles(Set.of(Usuario.Role.ROLE_USER))
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token")
    void deveAutenticarComSucesso() {
        AuthRequestDTO request = new AuthRequestDTO("isa@nucleo.com", "senha123");

        BDDMockito.given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(new UsernamePasswordAuthenticationToken("isa@nucleo.com", "senha123"));


        BDDMockito.given(usuarioRepository.findByEmailAndAtivoTrue("isa@nucleo.com"))
                .willReturn(Optional.of(usuarioExistente));

        BDDMockito.given(jwtTokenProvider.generateToken(any(UserDetailsImpl.class)))
                .willReturn("token123");

        AuthResponseDTO response = authService.autenticar(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).contains("Bearer");
        assertThat(response.getEmail()).isEqualTo("isa@nucleo.com");
    }

    @Test
    @DisplayName("Deve lançar erro ao autenticar com credenciais inválidas")
    void deveLancarErroQuandoCredenciaisInvalidas() {
        AuthRequestDTO request = new AuthRequestDTO("isa@nucleo.com", "senhaErrada");

        BDDMockito.willThrow(new AuthenticationException("login.login-failed"))
                .given(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(ResourceNotFoundException.class, () -> authService.autenticar(request));
    }

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void deveRegistrarNovoUsuario() {
        AuthRequestDTO request = new AuthRequestDTO("novo@nucleo.com", "senha123");

        BDDMockito.given(usuarioRepository.findByEmailAndAtivoTrue("novo@nucleo.com"))
                .willReturn(Optional.empty());

        BDDMockito.given(passwordEncoder.encode("senha123"))
                .willReturn("senhaCriptografada");

        BDDMockito.given(usuarioRepository.save(any(Usuario.class)))
                .willAnswer(invocation -> {
                    Usuario u = invocation.getArgument(0);
                    u.setId(10L);
                    return u;
                });

        BDDMockito.given(jwtTokenProvider.generateToken(any(UserDetailsImpl.class)))
                .willReturn("tokenNovoUsuario");

        AuthResponseDTO response = authService.registrar(request);

        assertThat(response.getToken()).contains("Bearer tokenNovoUsuario");
        assertThat(response.getEmail()).isEqualTo("novo@nucleo.com");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar registrar email já existente")
    void deveLancarErroAoRegistrarEmailExistente() {
        AuthRequestDTO request = new AuthRequestDTO("isa@nucleo.com", "senha123");

        BDDMockito.given(usuarioRepository.findByEmailAndAtivoTrue("isa@nucleo.com"))
                .willReturn(Optional.of(usuarioExistente));

        assertThrows(EntityNotCreatedException.class, () -> authService.registrar(request));
    }

    @Test
    @DisplayName("Deve lançar erro genérico ao falhar no registro")
    void deveLancarErroGenericoAoRegistrar() {
        AuthRequestDTO request = new AuthRequestDTO("erro@nucleo.com", "senha");

        BDDMockito.given(usuarioRepository.findByEmailAndAtivoTrue("erro@nucleo.com"))
                .willReturn(Optional.empty());

        BDDMockito.willThrow(new RuntimeException("Erro inesperado"))
                .given(usuarioRepository).save(any(Usuario.class));

        assertThrows(EntityNotCreatedException.class, () -> authService.registrar(request));
    }
}
