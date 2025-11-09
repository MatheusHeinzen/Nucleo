package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.model.Usuario;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;



    @BeforeEach
    void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils .class);
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        securityUtilsMock.when(SecurityUtils::isAdmin).thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        // fecha o mock estático para evitar conflitos
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }



    @Test
    @DisplayName("Deve listar todos os usuários (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosUsuarios() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                1L, "João Silva", "joao@nucleo.com",
                Set.of(Usuario.Role.ROLE_USER), true
        );

        BDDMockito.given(usuarioService.encontraTodosDTO()).willReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@nucleo.com"));
    }

    @Test
    @DisplayName("Deve retornar o usuário logado (USER)")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveBuscarUsuarioLogado() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                1L, "João Silva", "joao@nucleo.com",
                Set.of(Usuario.Role.ROLE_USER), true
        );

        BDDMockito.given(usuarioService.buscarPorId(1L)).willReturn(usuario);

        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@nucleo.com"));
    }

    @Test
    @DisplayName("Deve atualizar o usuário logado com sucesso")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveAtualizarUsuarioLogado() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO("João Atualizado", "joao@nucleo.com", "novaSenha", true);
        UsuarioResponseDTO response = new UsuarioResponseDTO(
                1L, "João Atualizado", "joao@nucleo.com",
                Set.of(Usuario.Role.ROLE_USER), true
        );

        BDDMockito.given(usuarioService.atualizaUsuario(request)).willReturn(response);

        mockMvc.perform(put("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"));
    }

    @Test
    @DisplayName("Deve deletar o usuário logado (USER)")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveDeletarUsuarioLogado() throws Exception {

        BDDMockito.willDoNothing().given(usuarioService).deletaUsuario(1L);

        mockMvc.perform(delete("/api/usuarios"))
                .andExpect(status().isOk());
    }
}
