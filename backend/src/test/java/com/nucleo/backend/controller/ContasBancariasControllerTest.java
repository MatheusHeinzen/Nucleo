package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.model.ContasBancarias;
import com.nucleo.model.TipoConta;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.ContasBancariasService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContasBancariasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContasBancariasService contasService;

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
    @DisplayName("Deve criar uma conta bancária para o usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveCriarContaBancaria() throws Exception {
        ContasBancarias conta = ContasBancarias.builder()
                .id(1L)
                .instituicao("Nubank")
                .tipo(TipoConta.CORRENTE)
                .apelido("Conta Principal")
                .moeda("BRL")
                .saldoInicial(new BigDecimal("1500.00"))
                .build();

        BDDMockito.given(contasService.criar(any(ContasBancarias.class))).willReturn(conta);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.instituicao").value("Nubank"))
                .andExpect(jsonPath("$.apelido").value("Conta Principal"));
    }

    @Test
    @DisplayName("Deve listar contas bancárias do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveListarMinhasContas() throws Exception {
        ContasBancarias conta = ContasBancarias.builder()
                .id(2L)
                .instituicao("Banco Inter")
                .tipo(TipoConta.POUPANCA)
                .apelido("Poupança")
                .saldoInicial(new BigDecimal("3000.00"))
                .build();

        BDDMockito.given(contasService.listarPorUsuario(1L)).willReturn(List.of(conta));

        mockMvc.perform(get("/api/contas/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].instituicao").value("Banco Inter"))
                .andExpect(jsonPath("$[0].tipo").value("POUPANCA"));
    }

    @Test
    @DisplayName("Deve listar todas as contas (ADMIN)")
    @WithMockUser(username = "admin@nucleo.com", roles = "ADMIN")
    void deveListarTodasContas() throws Exception {
        ContasBancarias conta = ContasBancarias.builder()
                .id(3L)
                .instituicao("Banco do Brasil")
                .tipo(TipoConta.CORRENTE)
                .apelido("Conta Salário")
                .saldoInicial(new BigDecimal("2000.00"))
                .build();

        BDDMockito.given(contasService.listarTodas()).willReturn(List.of(conta));

        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].instituicao").value("Banco do Brasil"))
                .andExpect(jsonPath("$[0].apelido").value("Conta Salário"));
    }

    @Test
    @DisplayName("Deve buscar conta bancária por ID")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveBuscarContaPorId() throws Exception {
        ContasBancarias conta = ContasBancarias.builder()
                .id(4L)
                .instituicao("Nubank")
                .tipo(TipoConta.CARTAO)
                .apelido("Cartão Roxinho")
                .moeda("BRL")
                .saldoInicial(BigDecimal.ZERO)
                .build();

        BDDMockito.given(contasService.buscarPorId(4L)).willReturn(conta);

        mockMvc.perform(get("/api/contas/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value("Cartão Roxinho"))
                .andExpect(jsonPath("$.tipo").value("CARTAO"));
    }

    @Test
    @DisplayName("Deve atualizar conta bancária do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveAtualizarConta() throws Exception {
        ContasBancarias atualizada = ContasBancarias.builder()
                .instituicao("Itau")
                .tipo(TipoConta.CORRENTE)
                .apelido("Conta Atualizada")
                .moeda("BRL")
                .saldoInicial(new BigDecimal("4000.00"))
                .build();

        BDDMockito.given(contasService.atualizar(eq(1L), any(ContasBancarias.class))).willReturn(atualizada);

        mockMvc.perform(put("/api/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizada)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instituicao").value("Itau"))
                .andExpect(jsonPath("$.apelido").value("Conta Atualizada"));

    }

    @Test
    @DisplayName("Deve deletar conta bancária do usuário logado")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveDeletarConta() throws Exception {
        mockMvc.perform(delete("/api/contas/5"))
                .andExpect(status().isOk());

    }

}
