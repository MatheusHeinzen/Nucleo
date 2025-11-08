package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.dto.AuthRequestDTO;
import com.nucleo.dto.AuthResponseDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // 🔹 Teste: login bem-sucedido
    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token JWT")
    void deveRealizarLoginComSucesso() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("joao@nucleo.com", "senha123");
        AuthResponseDTO response = new AuthResponseDTO("Bearer fake-jwt-token", "joao@nucleo.com");

        Mockito.when(authService.autenticar(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("Bearer fake-jwt-token"))
                .andExpect(jsonPath("$.email").value("joao@nucleo.com"));
    }

    // 🔹 Teste: login inválido
    @Test
    @DisplayName("Deve retornar erro 404 ao tentar logar com credenciais inválidas")
    void deveFalharLoginInvalido() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("invalido@nucleo.com", "senhaErrada");

        Mockito.when(authService.autenticar(any(AuthRequestDTO.class)))
                .thenThrow(new RuntimeException("login.login-failed"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    // 🔹 Teste: cadastro de novo usuário
    @Test
    @DisplayName("Deve registrar novo usuário e retornar token JWT")
    void deveRegistrarNovoUsuario() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("novo@nucleo.com", "senhaNova");
        AuthResponseDTO response = new AuthResponseDTO("Bearer token-novo", "novo@nucleo.com");

        Mockito.when(authService.registrar(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("Bearer token-novo"))
                .andExpect(jsonPath("$.email").value("novo@nucleo.com"));
    }

    // 🔹 Teste: falha ao registrar usuário existente
    @Test
    @DisplayName("Deve retornar erro ao tentar registrar um usuário já existente")
    void deveFalharAoRegistrarUsuarioExistente() throws Exception {
        AuthRequestDTO request = new AuthRequestDTO("joao@nucleo.com", "senha123");

        Mockito.when(authService.registrar(any(AuthRequestDTO.class)))
                .thenThrow(new EntityNotCreatedException("register.user_exists"));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
