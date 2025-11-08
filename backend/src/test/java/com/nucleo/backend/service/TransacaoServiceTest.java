package com.nucleo.backend.service;

import com.nucleo.dto.TransacaoRequestDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.model.Usuario;
import com.nucleo.repository.TransacaoRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.CategoriaService;
import com.nucleo.service.TransacaoService;
import com.nucleo.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class TransacaoServiceTest {

    @Autowired
    private TransacaoService transacaoService;

    @MockBean
    private TransacaoRepository transacaoRepository;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Categoria categoria;
    private Transacao transacao;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Isabel")
                .email("isa@nucleo.com")
                .senha("123456")
                .ativo(true)
                .build();

        categoria = Categoria.builder()
                .id(1L)
                .nome("Alimentação")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .build();

        transacao = Transacao.builder()
                .id(1L)
                .descricao("Supermercado")
                .valor(new BigDecimal("120.00"))
                .data(LocalDate.now())
                .tipo(Transacao.TipoTransacao.SAIDA)
                .usuario(usuario)
                .categoria(categoria)
                .ativo(true)
                .build();

        BDDMockito.mockStatic(SecurityUtils.class);
        BDDMockito.given(SecurityUtils.getCurrentUserId()).willReturn(1L);
        BDDMockito.given(SecurityUtils.isAdmin()).willReturn(false);
    }

    // ---------------------------
    // 🔹 CRIAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve criar transação com sucesso")
    void deveCriarTransacao() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getData(),
                transacao.getTipo(),
                1L,
                null
        );

        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.given(categoriaService.buscarPorId(1L)).willReturn(categoria);
        BDDMockito.given(transacaoRepository.save(any(Transacao.class))).willReturn(transacao);

        Transacao resultado = transacaoService.criar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescricao()).isEqualTo("Supermercado");
        assertThat(resultado.getCategoria().getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na criação da transação")
    void deveLancarErroAoCriarTransacao() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Restaurante", new BigDecimal("80.00"),
                LocalDate.now(), Transacao.TipoTransacao.SAIDA,
                1L, null
        );

        BDDMockito.willThrow(new RuntimeException("Erro"))
                .given(transacaoRepository).save(any(Transacao.class));

        assertThrows(EntityNotCreatedException.class, () -> transacaoService.criar(request));
    }

    // ---------------------------
    // 🔹 LISTAGEM
    // ---------------------------

    @Test
    @DisplayName("Deve listar todas as transações do usuário")
    void deveListarTransacoesUsuario() {
        BDDMockito.given(transacaoRepository.findByUsuarioId(1L))
                .willReturn(List.of(transacao));

        List<Transacao> resultado = transacaoService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDescricao()).isEqualTo("Supermercado");
    }

    @Test
    @DisplayName("Deve listar todas as transações de um usuário específico (admin)")
    void deveListarTodasPorUsuarioId() {
        BDDMockito.given(transacaoRepository.findByUsuarioId(1L))
                .willReturn(List.of(transacao));

        List<Transacao> resultado = transacaoService.listarTodas(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValor()).isEqualByComparingTo("120.00");
    }

    // ---------------------------
    // 🔹 BUSCA
    // ---------------------------

    @Test
    @DisplayName("Deve buscar transação por ID do usuário logado")
    void deveBuscarPorIdEUsuario() {
        BDDMockito.given(transacaoRepository.findById(1L))
                .willReturn(Optional.of(transacao));

        Transacao resultado = transacaoService.buscarPorIdEUsuario(1L, 1L);

        assertThat(resultado.getDescricao()).isEqualTo("Supermercado");
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar transação inexistente")
    void deveLancarErroAoBuscarInexistente() {
        BDDMockito.given(transacaoRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transacaoService.buscarPorIdEUsuario(99L, 1L));
    }

    // ---------------------------
    // 🔹 ATUALIZAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve atualizar transação existente com sucesso")
    void deveAtualizarTransacao() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Supermercado Atualizado",
                new BigDecimal("130.00"),
                LocalDate.now(),
                Transacao.TipoTransacao.SAIDA,
                1L,
                null
        );

        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.given(categoriaService.buscarPorId(1L)).willReturn(categoria);
        BDDMockito.given(transacaoRepository.findById(1L))
                .willReturn(Optional.of(transacao));
        BDDMockito.given(transacaoRepository.save(any(Transacao.class)))
                .willReturn(transacao);

        Transacao resultado = transacaoService.atualizar(1L, request);

        assertThat(resultado.getDescricao()).contains("Atualizado");
        assertThat(resultado.getValor()).isEqualByComparingTo("130.00");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar transação inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Restaurante",
                new BigDecimal("90.00"),
                LocalDate.now(),
                Transacao.TipoTransacao.SAIDA,
                1L,
                null
        );

        BDDMockito.given(transacaoRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotUpdatedException.class,
                () -> transacaoService.atualizar(99L, request));
    }

    // ---------------------------
    // 🔹 EXCLUSÃO
    // ---------------------------

    @Test
    @DisplayName("Deve excluir transação existente com sucesso")
    void deveExcluirTransacao() {
        BDDMockito.given(transacaoRepository.findById(1L)).willReturn(Optional.of(transacao));

        transacaoService.excluir(1L);

        BDDMockito.then(transacaoRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao excluir transação inexistente")
    void deveLancarErroAoExcluirInexistente() {
        BDDMockito.given(transacaoRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotDeletedException.class,
                () -> transacaoService.excluir(99L));
    }
}
