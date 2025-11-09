package com.nucleo.backend.service;

import com.nucleo.model.ContasBancarias;
import com.nucleo.model.TipoConta;
import com.nucleo.model.Usuario;
import com.nucleo.repository.ContasBancariasRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.ContasBancariasService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class ContasBancariasServiceTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @Autowired
    private ContasBancariasService contasService;

    @MockBean
    private ContasBancariasRepository contasRepository;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private ContasBancarias conta;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Isabel")
                .email("isa@nucleo.com")
                .senha("123456")
                .ativo(true)
                .build();

        conta = ContasBancarias.builder()
                .id(1L)
                .instituicao("Nubank")
                .tipo(TipoConta.CORRENTE)
                .apelido("Conta Principal")
                .saldoInicial(new BigDecimal("2500.00"))
                .moeda("BRL")
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
    @DisplayName("Deve criar nova conta bancária para o usuário logado")
    void deveCriarContaBancaria() {
        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.given(contasRepository.save(any(ContasBancarias.class))).willReturn(conta);

        ContasBancarias resultado = contasService.criar(conta);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getInstituicao()).isEqualTo("Nubank");
        assertThat(resultado.getUsuario().getNome()).isEqualTo("Isabel");
    }

    // ---------------------------
    // 🔹 LISTAGEM
    // ---------------------------

    @Test
    @DisplayName("Deve listar contas bancárias do usuário")
    void deveListarContasDoUsuario() {
        BDDMockito.given(contasRepository.findByUsuarioIdAndAtivoTrue(1L))
                .willReturn(List.of(conta));

        List<ContasBancarias> resultado = contasService.listarPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getInstituicao()).isEqualTo("Nubank");
    }

    @Test
    @DisplayName("Deve listar todas as contas (admin)")
    void deveListarTodasContas() {
        BDDMockito.given(contasRepository.findAll()).willReturn(List.of(conta));

        List<ContasBancarias> resultado = contasService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getApelido()).isEqualTo("Conta Principal");
    }

    // ---------------------------
    // 🔹 BUSCA
    // ---------------------------

    @Test
    @DisplayName("Deve buscar conta por ID do usuário")
    void deveBuscarContaPorIdUsuario() {
        BDDMockito.given(contasRepository.findByIdAndUsuarioIdAndAtivoTrue(1L, 1L))
                .willReturn(Optional.of(conta));

        ContasBancarias resultado = contasService.buscarPorId(1L, 1L, false);

        assertThat(resultado.getInstituicao()).isEqualTo("Nubank");
    }

    @Test
    @DisplayName("Deve buscar conta por ID como administrador")
    void deveBuscarContaComoAdmin() {
        BDDMockito.given(contasRepository.findById(1L))
                .willReturn(Optional.of(conta));

        ContasBancarias resultado = contasService.buscarPorId(1L, 1L, true);

        assertThat(resultado.getTipo()).isEqualTo(TipoConta.CORRENTE);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar conta inexistente")
    void deveLancarErroAoBuscarContaInexistente() {
        BDDMockito.given(contasRepository.findByIdAndUsuarioIdAndAtivoTrue(99L, 1L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contasService.buscarPorId(99L, 1L, false));
    }

    // ---------------------------
    // 🔹 ATUALIZAÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve atualizar conta bancária existente")
    void deveAtualizarContaBancaria() {
        ContasBancarias atualizada = ContasBancarias.builder()
                .id(1L)
                .instituicao("Banco Inter")
                .tipo(TipoConta.POUPANCA)
                .apelido("Conta Poupança")
                .saldoInicial(new BigDecimal("3000.00"))
                .usuario(usuario)
                .ativo(true)
                .build();

        BDDMockito.given(contasRepository.findByIdAndUsuarioIdAndAtivoTrue(1L, 1L))
                .willReturn(Optional.of(conta));
        BDDMockito.given(contasRepository.save(any(ContasBancarias.class))).willReturn(atualizada);

        ContasBancarias resultado = contasService.atualizar(1L, atualizada, 1L, false);

        assertThat(resultado.getInstituicao()).isEqualTo("Banco Inter");
        assertThat(resultado.getApelido()).isEqualTo("Conta Poupança");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar conta inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        ContasBancarias atualizada = ContasBancarias.builder()
                .instituicao("Itau")
                .tipo(TipoConta.CORRENTE)
                .build();

        BDDMockito.given(contasRepository.findByIdAndUsuarioIdAndAtivoTrue(99L, 1L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contasService.atualizar(99L, atualizada, 1L, false));
    }

    // ---------------------------
    // 🔹 DELEÇÃO
    // ---------------------------

    @Test
    @DisplayName("Deve deletar conta bancária do usuário")
    void deveDeletarContaUsuario() {
        BDDMockito.given(contasRepository.findByIdAndUsuarioId(1L, 1L))
                .willReturn(Optional.of(conta));

        BDDMockito.given(contasRepository.save(any(ContasBancarias.class)))
                .willReturn(conta);

        contasService.deletar(1L, 1L, false);

        assertThat(conta.getAtivo()).isFalse();
        assertThat(conta.getDeletadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar conta como admin")
    void deveDeletarContaAdmin() {
        BDDMockito.given(contasRepository.findById(1L)).willReturn(Optional.of(conta));
        BDDMockito.given(contasRepository.save(any(ContasBancarias.class))).willReturn(conta);

        contasService.deletar(1L, 1L, true);

        assertThat(conta.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar conta inexistente")
    void deveLancarErroAoDeletarContaInexistente() {
        BDDMockito.given(contasRepository.findByIdAndUsuarioId(99L, 1L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contasService.deletar(99L, 1L, false));
    }
}
