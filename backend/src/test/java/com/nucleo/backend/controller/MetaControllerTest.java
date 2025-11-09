package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.MetaService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetaService metaService;

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
    @DisplayName("Deve criar uma nova meta")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveCriarMeta() throws Exception {
        Meta meta = Meta.builder()
                .id(1L)
                .usuarioId(1L)
                .titulo("Juntar para viagem")
                .valorAlvo(new BigDecimal("5000.00"))
                .dataLimite(LocalDate.now().plusMonths(6))
                .categoriaId(1L)
                .status(StatusMeta.ativa)
                .build();

        BDDMockito.given(metaService.criar(any(Meta.class))).willReturn(meta);

        mockMvc.perform(post("/api/metas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Juntar para viagem"))
                .andExpect(jsonPath("$.valorAlvo").value(5000.00));
    }

    @Test
    @DisplayName("Deve listar metas do usuário logado")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveListarMetasDoUsuario() throws Exception {
        List<Meta> metas = List.of(
                Meta.builder().id(1L).titulo("Comprar câmera nova").valorAlvo(new BigDecimal("3000")).status(StatusMeta.ativa).build(),
                Meta.builder().id(2L).titulo("Viagem 2026").valorAlvo(new BigDecimal("8000")).status(StatusMeta.ativa).build()
        );

        BDDMockito.given(metaService.listarPorUsuario(any(Long.class))).willReturn(metas);

        mockMvc.perform(get("/api/metas/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Comprar câmera nova"))
                .andExpect(jsonPath("$[1].titulo").value("Viagem 2026"));
    }

    @Test
    @DisplayName("Deve buscar meta por ID")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveBuscarMetaPorId() throws Exception {
        Meta meta = Meta.builder()
                .id(1L)
                .titulo("Nova lente")
                .valorAlvo(new BigDecimal("2000.00"))
                .status(StatusMeta.ativa)
                .build();

        BDDMockito.given(metaService.buscarPorId(eq(1L), any(Long.class))).willReturn(meta);

        mockMvc.perform(get("/api/metas/1/userid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Nova lente"));
    }

    @Test
    @DisplayName("Deve atualizar meta existente")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveAtualizarMeta() throws Exception {
        Meta metaAtualizada = Meta.builder()
                .id(1L)
                .titulo("Juntar para intercâmbio")
                .valorAlvo(new BigDecimal("7000.00"))
                .status(StatusMeta.ativa)
                .build();

        BDDMockito.given(metaService.atualizar(eq(1L), any(Meta.class), any(Long.class))).willReturn(metaAtualizada);

        mockMvc.perform(put("/api/metas/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Juntar para intercâmbio"))
                .andExpect(jsonPath("$.valorAlvo").value(7000.00));
    }

    @Test
    @DisplayName("Deve cancelar (deletar logicamente) uma meta")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveCancelarMeta() throws Exception {
        mockMvc.perform(delete("/api/metas/1/1"))
                .andExpect(status().isNoContent());
    }
}
