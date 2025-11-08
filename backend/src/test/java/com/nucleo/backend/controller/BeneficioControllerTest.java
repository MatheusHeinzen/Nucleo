package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.model.Beneficio;
import com.nucleo.service.BeneficioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeneficioService beneficioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um novo benefício para o usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveCriarBeneficio() throws Exception {
        Beneficio novo = Beneficio.builder()
                .id(1L)
                .nome("Vale Refeição")
                .descricao("Cartão VR da empresa")
                .tipo(Beneficio.TipoBeneficio.VR)
                .valor(new BigDecimal("500.00"))
                .build();

        BDDMockito.given(beneficioService.criarParaUsuarioLogado(novo)).willReturn(novo);

        mockMvc.perform(post("/api/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Vale Refeição"))
                .andExpect(jsonPath("$.valor").value(500.00));
    }

    @Test
    @DisplayName("Deve listar benefícios do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveListarBeneficiosDoUsuario() throws Exception {
        Beneficio beneficio = Beneficio.builder()
                .id(1L)
                .nome("Vale Transporte")
                .descricao("VT mensal")
                .tipo(Beneficio.TipoBeneficio.VT)
                .valor(new BigDecimal("200.00"))
                .build();

        BDDMockito.given(beneficioService.buscarPorUsuarioLogado()).willReturn(List.of(beneficio));

        mockMvc.perform(get("/api/beneficios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Vale Transporte"))
                .andExpect(jsonPath("$[0].valor").value(200.00));
    }

    @Test
    @DisplayName("Deve listar todos os benefícios (ADMIN)")
    @WithMockUser(username = "admin@nucleo.com", roles = "ADMIN")
    void deveListarTodosBeneficios() throws Exception {
        Beneficio beneficio = Beneficio.builder()
                .id(2L)
                .nome("Plano de Saúde")
                .descricao("Unimed nacional")
                .tipo(Beneficio.TipoBeneficio.PLANO_SAUDE)
                .valor(new BigDecimal("350.00"))
                .build();

        BDDMockito.given(beneficioService.listarTodos()).willReturn(List.of(beneficio));

        mockMvc.perform(get("/api/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Plano de Saúde"))
                .andExpect(jsonPath("$[0].descricao").value("Unimed nacional"));
    }

    @Test
    @DisplayName("Deve buscar benefício por ID")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveBuscarBeneficioPorId() throws Exception {
        Beneficio beneficio = Beneficio.builder()
                .id(3L)
                .nome("Gympass")
                .descricao("Acesso a academias conveniadas")
                .tipo(Beneficio.TipoBeneficio.GYMPASS)
                .valor(new BigDecimal("120.00"))
                .build();

        BDDMockito.given(beneficioService.buscarPorIdEUsuario(3L, 1L, false)).willReturn(beneficio);

        mockMvc.perform(get("/api/beneficios/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gympass"))
                .andExpect(jsonPath("$.tipo").value("GYMPASS"));
    }

    @Test
    @DisplayName("Deve atualizar um benefício do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveAtualizarBeneficioUsuario() throws Exception {
        Beneficio atualizado = Beneficio.builder()
                .id(1L)
                .nome("Vale Refeição Atualizado")
                .descricao("Novo valor ajustado")
                .tipo(Beneficio.TipoBeneficio.VR)
                .valor(new BigDecimal("600.00"))
                .build();

        BDDMockito.given(beneficioService.atualizarMeu(1L, atualizado, 1L, false)).willReturn(atualizado);

        mockMvc.perform(put("/api/beneficios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Vale Refeição Atualizado"))
                .andExpect(jsonPath("$.valor").value(600.00));
    }

    @Test
    @DisplayName("Deve deletar benefício do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveDeletarBeneficioUsuario() throws Exception {
        mockMvc.perform(delete("/api/beneficios/1"))
                .andExpect(status().isNoContent());
    }
}
