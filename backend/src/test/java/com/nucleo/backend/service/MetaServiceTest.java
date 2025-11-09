package com.nucleo.backend.service;


import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import com.nucleo.repository.MetaRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.MetaService;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class MetaServiceTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;


    @Autowired
    private MetaService metaService;

    @MockBean
    private MetaRepository metaRepository;

    @MockBean
    private SecurityUtils securityUtils;

    private Meta meta;

    @BeforeEach
    void setup() {
        meta = Meta.builder()
                .id(1L)
                .usuarioId(1L)
                .titulo("Viagem para Europa")
                .valorAlvo(new BigDecimal("15000.00"))
                .dataLimite(LocalDate.now().plusMonths(6))
                .categoriaId(1L)
                .status(StatusMeta.ativa)
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
    @DisplayName("Deve criar meta com sucesso")
    void deveCriarMeta() {
        BDDMockito.given(metaRepository.save(any(Meta.class))).willReturn(meta);

        Meta resultado = metaService.criar(meta);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("Viagem para Europa");
        assertThat(resultado.getStatus()).isEqualTo(StatusMeta.ativa);
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na criação da meta")
    void deveLancarErroAoCriarMeta() {
        BDDMockito.willThrow(new RuntimeException("Erro ao salvar")).given(metaRepository).save(any(Meta.class));

        assertThrows(EntityNotCreatedException.class, () -> metaService.criar(meta));
    }

    // ---------------------------
    // 🔹 LISTAGEM
    // ---------------------------

    @Test
    @DisplayName("Deve listar metas por usuário")
    void deveListarMetasPorUsuario() {
        BDDMockito.given(metaRepository.findByUsuarioId(1L)).willReturn(List.of(meta));

        List<Meta> lista = metaService.listarPorUsuario(1L);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getTitulo()).isEqualTo("Viagem para Europa");
    }

    @Test
    @DisplayName("Deve listar todas as metas (admin)")
    void deveListarTodasMetas() {
        BDDMockito.given(metaRepository.findAll()).willReturn(List.of(meta));

        List<Meta> lista = metaService.listarTodas();

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getValorAlvo()).isEqualByComparingTo("15000.00");
    }

    // ---------------------------
    // 🔹 BUSCA
    // ---------------------------

    @Test
    @DisplayName("Deve buscar meta por ID e usuário")
    void deveBuscarMetaPorId() {
        BDDMockito.given(metaRepository.findByUsuarioIdAndId(1L, 1L)).willReturn(meta);

        Meta resultado = metaService.buscarPorId(1L, 1L);

        assertThat(resultado.getTitulo()).isEqualTo("Viagem para Europa");
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar meta que não pertence ao usuário")
    void deveLancarErroAoBuscarMetaDeOutroUsuario() {
        Meta outraMeta = Meta.builder()
                .id(2L)
                .usuarioId(2L)
                .titulo("Meta Inválida")
                .build();

        BDDMockito.given(metaRepository.findByUsuarioIdAndId(1L, 1L)).willReturn(outraMeta);

        assertThrows(EntityNotFoundException.class, () -> metaService.buscarPorId(1L, 1L));
    }

    // ---------------------------
    // 🔹 ATUALIZAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve atualizar meta existente")
    void deveAtualizarMeta() {
        Meta atualizada = Meta.builder()
                .id(1L)
                .usuarioId(1L)
                .titulo("Viagem Atualizada")
                .valorAlvo(new BigDecimal("20000.00"))
                .dataLimite(LocalDate.now().plusMonths(8))
                .categoriaId(1L)
                .status(StatusMeta.ativa)
                .build();

        BDDMockito.given(metaRepository.findByUsuarioIdAndId(1L, 1L)).willReturn(meta);
        BDDMockito.given(metaRepository.save(any(Meta.class))).willReturn(atualizada);

        Meta resultado = metaService.atualizar(1L, atualizada, 1L);

        assertThat(resultado.getTitulo()).isEqualTo("Viagem Atualizada");
        assertThat(resultado.getValorAlvo()).isEqualByComparingTo("20000.00");
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar meta inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        BDDMockito.given(metaRepository.findByUsuarioIdAndId(1L, 1L)).willThrow(new EntityNotFoundException("Meta não encontrada"));

        assertThrows(EntityNotUpdatedException.class, () -> metaService.atualizar(1L, meta, 1L));
    }

    // ---------------------------
    // 🔹 CANCELAMENTO
    // ---------------------------

    @Test
    @DisplayName("Deve cancelar meta do usuário logado")
    void deveCancelarMeta() {
        BDDMockito.given(metaRepository.findByUsuarioIdAndId(1L, 1L)).willReturn(meta);
        BDDMockito.given(metaRepository.save(any(Meta.class))).willReturn(meta);

        metaService.cancelar(1L, 1L);

        assertThat(meta.getStatus()).isEqualTo(StatusMeta.cancelada);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cancelar meta inexistente")
    void deveLancarErroAoCancelarInexistente() {
        BDDMockito.willThrow(new RuntimeException("Erro")).given(metaRepository).findByUsuarioIdAndId(1L, 1L);

        assertThrows(EntityNotDeletedException.class, () -> metaService.cancelar(1L, 1L));
    }
}
