package com.nucleo.backend.service;


import com.nucleo.dto.CategoriaRequestDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.model.Usuario;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.CategoriaService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
class CategoriaServiceTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @Autowired
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Isabel")
                .email("isa@nucleo.com")
                .senha("senha123")
                .ativo(true)
                .build();

        categoria = Categoria.builder()
                .id(1L)
                .nome("Alimentação")
                .descricao("Gastos com comida e mercado")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .isGlobal(false)
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

    @Test
    @DisplayName("Deve criar nova categoria pessoal com sucesso")
    void deveCriarCategoriaPessoal() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Transporte", "Gastos com deslocamento", Categoria.TipoCategoria.SAIDA);

        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.given(categoriaRepository.save(any(Categoria.class))).willReturn(categoria);

        Categoria resultado = categoriaService.criar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Alimentação");
        assertThat(resultado.getUsuario().getNome()).isEqualTo("Isabel");
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na criação da categoria")
    void deveLancarErroAoCriarCategoria() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Saúde", "Consultas e remédios", Categoria.TipoCategoria.SAIDA);

        BDDMockito.given(usuarioService.buscarEntidadePorId(1L)).willReturn(usuario);
        BDDMockito.willThrow(new RuntimeException("Erro interno")).given(categoriaRepository).save(any(Categoria.class));

        assertThrows(EntityNotCreatedException.class, () -> categoriaService.criar(dto));
    }

    @Test
    @DisplayName("Deve listar categorias do usuário e globais")
    void deveListarCategoriasDoUsuario() {
        List<Categoria> categorias = List.of(categoria);

        BDDMockito.given(categoriaRepository.findByIsGlobalTrueOrUsuarioIdAndAtivoTrue(1L)).willReturn(categorias);

        List<Categoria> resultado = categoriaService.listarPorUsuario();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve buscar categoria por ID")
    void deveBuscarCategoriaPorId() {
        BDDMockito.given(categoriaRepository.findByIdAndAtivoTrue(1L)).willReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1L);

        assertThat(resultado.getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar categoria inexistente")
    void deveLancarErroAoBuscarCategoriaInexistente() {
        BDDMockito.given(categoriaRepository.findByIdAndAtivoTrue(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoriaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo")
    void deveBuscarPorTipo() {
        BDDMockito.given(categoriaRepository.findByIsGlobalTrueOrUsuarioIdAndTipoAndAtivoTrue(1L, Categoria.TipoCategoria.SAIDA))
                .willReturn(List.of(categoria));

        List<Categoria> resultado = categoriaService.buscarPorTipo(Categoria.TipoCategoria.SAIDA);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getTipo()).isEqualTo(Categoria.TipoCategoria.SAIDA);
    }

    @Test
    @DisplayName("Deve atualizar categoria existente")
    void deveAtualizarCategoria() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "Passeios e hobbies", Categoria.TipoCategoria.SAIDA);

        BDDMockito.given(categoriaRepository.findByIdAndAtivoTrue(1L)).willReturn(Optional.of(categoria));
        BDDMockito.given(categoriaService.criar(dto)).willReturn(categoria);

        Categoria resultado = categoriaService.atualizar(1L, dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Lazer");


    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar categoria inexistente")
    void deveLancarErroAoAtualizarCategoriaInexistente() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer", "Passeios e hobbies", Categoria.TipoCategoria.SAIDA);

        BDDMockito.given(categoriaRepository.findByIdAndAtivoTrue(99L)).willReturn(Optional.empty());

        assertThrows(EntityNotUpdatedException.class, () -> categoriaService.atualizar(99L, dto));
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void deveDeletarCategoria() {
        BDDMockito.doNothing().when(categoriaRepository).softDelete(1L);

        categoriaService.deletar(1L);

        BDDMockito.then(categoriaRepository).should().softDelete(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao falhar na deleção")
    void deveLancarErroAoFalharDelecao() {
        BDDMockito.willThrow(new RuntimeException("Erro ao deletar")).given(categoriaRepository).softDelete(1L);

        assertThrows(EntityNotDeletedException.class, () -> categoriaService.deletar(1L));
    }
}
