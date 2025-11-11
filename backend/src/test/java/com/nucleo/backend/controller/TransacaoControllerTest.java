package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.dto.TransacaoRequestDTO;
import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.model.Usuario;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.TransacaoService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransacaoControllerTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
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
    @DisplayName("Deve criar uma nova transação")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveCriarTransacao() throws Exception {
        // Cria uma transação simulada (resposta esperada)
        Transacao transacao = Transacao.builder()
                .id(1L)
                .descricao("Compra no mercado")
                .valor(new BigDecimal("150.50"))
                .data(LocalDate.now())
                .tipo(Transacao.TipoTransacao.SAIDA)
                .categoria(Categoria.builder().id(1L).nome("Alimentação").build())
                .build();

        // Cria o DTO usando os valores literais
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Compra no mercado",
                new BigDecimal("150.50"),
                LocalDate.now(),
                Transacao.TipoTransacao.SAIDA,
                1L,
                1L
        );

        // Configura o mock da service
        BDDMockito.given(transacaoService.criar(request)).willReturn(transacao);

        // Executa a requisição simulada e verifica o retorno
        mockMvc.perform(post("/api/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Compra no mercado"))
                .andExpect(jsonPath("$.valor").value(150.50));
    }

    @Test
    @DisplayName("Deve listar todas as transações do usuário")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveListarTransacoes() throws Exception {
        Transacao t1 = Transacao.builder()
                .id(2L)
                .descricao("Salário mensal")
                .valor(new BigDecimal("5000.00"))
                .data(LocalDate.now().minusDays(3))
                .tipo(Transacao.TipoTransacao.ENTRADA)
                .categoria(Categoria.builder().id(1L).nome("Alimentacao").build())
                .usuario(Usuario.builder().id(1L).build())
                .build();

        Transacao t2 = Transacao.builder()
                .id(3L)
                .descricao("Aluguel")
                .valor(new BigDecimal("1200.00"))
                .data(LocalDate.now().minusDays(1))
                .tipo(Transacao.TipoTransacao.SAIDA)
                .categoria(Categoria.builder().id(1L).nome("Alimentacao").build())
                .usuario(Usuario.builder().id(1L).build())
                .build();

        BDDMockito.given(transacaoService.listarTodas()).willReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/transacoes/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Salário mensal"))
                .andExpect(jsonPath("$[1].descricao").value("Aluguel"));
    }

    @Test
    @DisplayName("Deve buscar uma transação pelo ID")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveBuscarTransacaoPorId() throws Exception {
        Transacao transacao = Transacao.builder()
                .id(4L)
                .descricao("Cinema IMAX")
                .valor(new BigDecimal("80.00"))
                .data(LocalDate.now().minusDays(1))
                .tipo(Transacao.TipoTransacao.SAIDA)
                .usuario(Usuario.builder().id(1L).build())
                .build();

        BDDMockito.given(transacaoService.buscarPorIdEUsuario(4L)).willReturn(transacao);

        mockMvc.perform(get("/api/transacoes/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Cinema IMAX"))
                .andExpect(jsonPath("$.valor").value(80.00));
    }

    @Test
    @DisplayName("Deve atualizar uma transação existente")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveAtualizarTransacao() throws Exception {
        // Record TransacaoRequestDTO sendo criado corretamente
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Supermercado Atualizado",
                new BigDecimal("200.00"),
                LocalDate.now(),
                Transacao.TipoTransacao.SAIDA,
                1L,
                1L
        );

        // Criação da entidade esperada (resposta simulada)
        Transacao atualizada = Transacao.builder()
                .id(5L)
                .descricao(request.descricao()) // acessando campos do record
                .valor(request.valor())
                .data(request.data())
                .tipo(request.tipo())
                .categoria(Categoria.builder().id(request.categoriaId()).nome("Alimentação").build())
                .build();

        // Mock da service
        BDDMockito.given(transacaoService.atualizar(5L, request)).willReturn(atualizada);

        // Execução do teste
        mockMvc.perform(put("/api/transacoes/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Supermercado Atualizado"))
                .andExpect(jsonPath("$.valor").value(200.00));
    }

    @Test
    @DisplayName("Deve excluir uma transação pelo ID")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveExcluirTransacao() throws Exception {
        mockMvc.perform(delete("/api/transacoes/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar transações por tipo (ENTRADA)")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveBuscarPorTipo() throws Exception {
        Transacao t = Transacao.builder()
                .id(6L)
                .descricao("Venda de projeto")
                .valor(new BigDecimal("2500.00"))
                .tipo(Transacao.TipoTransacao.ENTRADA)
                .data(LocalDate.now())
                .usuario(Usuario.builder().id(1L).build())
                .build();

        BDDMockito.given(transacaoService.encontraPorTipo(Transacao.TipoTransacao.ENTRADA)).willReturn(List.of(t));

        mockMvc.perform(get("/api/transacoes/me/tipo/ENTRADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Venda de projeto"))
                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"));
    }

    @Test
    @DisplayName("Deve retornar resumo financeiro do usuário")
    @WithMockUser(username = "joao@nucleo.com", roles = "USER")
    void deveRetornarResumoFinanceiro() throws Exception {
        BDDMockito.given(transacaoService.getTotalEntradas(1L)).willReturn(new BigDecimal("5000.00"));
        BDDMockito.given(transacaoService.getTotalSaidas(1L)).willReturn(new BigDecimal("3000.00"));
        BDDMockito.given(transacaoService.getSaldo(1L)).willReturn(new BigDecimal("2000.00"));

        mockMvc.perform(get("/api/transacoes/me/resumo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Entradas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saídas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Saldo")));
    }
}
