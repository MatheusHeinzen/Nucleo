package com.nucleo.backend.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Beneficio;
import com.nucleo.model.Usuario;
import com.nucleo.repository.BeneficioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.BeneficioService;
import com.nucleo.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class BeneficioServiceTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @Autowired
    private BeneficioService beneficioService;

    @MockBean
    private BeneficioRepository beneficioRepository;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Beneficio beneficio;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Isabel")
                .email("isa@nucleo.com")
                .senha("123456")
                .ativo(true)
                .build();

        beneficio = Beneficio.builder()
                .id(1L)
                .nome("Vale Refeição")
                .descricao("Cartão de alimentação")
                .tipo(Beneficio.TipoBeneficio.VR)
                .valor(new BigDecimal("500.00"))
                .usuario(usuario)
                .ativo(true)
                .build();

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

    // ---------------------------
    // 🔹 CRIAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve criar benefício para o usuário logado com sucesso")
    void deveCriarBeneficioParaUsuarioLogado() {
        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.given(beneficioRepository.save(any(Beneficio.class))).willReturn(beneficio);

        Beneficio resultado = beneficioService.criarParaUsuarioLogado(beneficio);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Vale Refeição");
        assertThat(resultado.getUsuario().getNome()).isEqualTo("Isabel");
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na criação do benefício")
    void deveLancarErroAoCriarBeneficio() {
        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.willThrow(new RuntimeException("Erro interno"))
                .given(beneficioRepository).save(any(Beneficio.class));

        assertThrows(EntityNotCreatedException.class,
                () -> beneficioService.criarParaUsuarioLogado(beneficio));
    }

    // ---------------------------
    // 🔹 BUSCAS
    // ---------------------------

    @Test
    @DisplayName("Deve listar todos os benefícios ativos")
    void deveListarTodos() {
        BDDMockito.given(beneficioRepository.findAllByAtivoTrue())
                .willReturn(List.of(beneficio));

        List<Beneficio> lista = beneficioService.listarTodos();

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getNome()).isEqualTo("Vale Refeição");
    }

    @Test
    @DisplayName("Deve buscar benefício por ID com sucesso")
    void deveBuscarPorId() {
        BDDMockito.given(beneficioRepository.findByIdAndAtivoTrue(1L))
                .willReturn(Optional.of(beneficio));

        Beneficio encontrado = beneficioService.buscarPorId(1L);

        assertThat(encontrado.getNome()).isEqualTo("Vale Refeição");
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar benefício inexistente")
    void deveLancarErroAoBuscarBeneficioInexistente() {
        BDDMockito.given(beneficioRepository.findByIdAndAtivoTrue(99L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> beneficioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar benefícios do usuário logado")
    void deveBuscarBeneficiosPorUsuarioLogado() {
        BDDMockito.given(beneficioRepository.findByUsuarioIdAndAtivoTrue(1L))
                .willReturn(List.of(beneficio));

        List<Beneficio> lista = beneficioService.buscarPorUsuarioLogado();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getUsuario().getNome()).isEqualTo("Isabel");
    }

    // ---------------------------
    // 🔹 ATUALIZAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve atualizar benefício do usuário logado com sucesso")
    void deveAtualizarBeneficioDoUsuario() {
        Beneficio atualizado = Beneficio.builder()
                .id(1L)
                .nome("Vale Refeição Atualizado")
                .descricao("Benefício reajustado")
                .tipo(Beneficio.TipoBeneficio.VR)
                .valor(new BigDecimal("600.00"))
                .usuario(usuario)
                .build();

        BDDMockito.given(beneficioRepository.findByIdAndAtivoTrue(1L))
                .willReturn(Optional.of(beneficio));
        BDDMockito.given(beneficioRepository.save(any(Beneficio.class)))
                .willReturn(atualizado);

        Beneficio resultado = beneficioService.atualizarMeu(1L, atualizado);

        assertThat(resultado.getNome()).contains("Atualizado");
        assertThat(resultado.getValor()).isEqualByComparingTo("600.00");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar benefício inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        Beneficio atualizado = Beneficio.builder()
                .id(99L)
                .nome("Inexistente")
                .build();

        BDDMockito.given(beneficioRepository.findByIdAndAtivoTrue(99L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotUpdatedException.class,
                () -> beneficioService.atualizarMeu(99L, atualizado));
    }

    // ---------------------------
    // 🔹 DELEÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve deletar benefício com sucesso")
    void deveDeletarBeneficio() {
        BDDMockito.given(beneficioRepository.findByIdAndAtivoTrue(1L))
                .willReturn(Optional.of(beneficio));

        BDDMockito.doNothing().when(beneficioRepository).softDelete(1L);

        beneficioService.deletarMeu(1L);

        BDDMockito.then(beneficioRepository).should().softDelete(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na deleção do benefício")
    void deveLancarErroAoFalharDelecao() {
        BDDMockito.willThrow(new RuntimeException("Erro ao deletar"))
                .given(beneficioRepository).softDelete(1L);

        assertThrows(EntityNotDeletedException.class,
                () -> beneficioService.deletarMeu(1L));
    }
}
