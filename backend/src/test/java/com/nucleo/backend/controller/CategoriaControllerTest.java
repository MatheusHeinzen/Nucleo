package com.nucleo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nucleo.dto.CategoriaRequestDTO;
import com.nucleo.dto.CategoriaResponseDTO;
import com.nucleo.model.Categoria;
import com.nucleo.service.CategoriaService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma nova categoria para o usuário logado")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveCriarCategoria() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Saúde", "Gastos médicos", Categoria.TipoCategoria.SAIDA);
        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Saúde")
                .descricao("Gastos médicos")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .isGlobal(false)
                .build();

        BDDMockito.given(categoriaService.criar(request)).willReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Saúde"))
                .andExpect(jsonPath("$.descricao").value("Gastos médicos"));
    }

    @Test
    @DisplayName("Deve listar categorias disponíveis para o usuário logado")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveListarCategoriasUsuario() throws Exception {
        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Alimentação")
                .descricao("Comidas e refeições")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .isGlobal(true)
                .build();

        BDDMockito.given(categoriaService.listarPorUsuario()).willReturn(List.of(categoria));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Alimentação"))
                .andExpect(jsonPath("$[0].tipo").value("SAIDA"));
    }

    @Test
    @DisplayName("Deve buscar categoria por ID")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveBuscarCategoriaPorId() throws Exception {
        Categoria categoria = Categoria.builder()
                .id(2L)
                .nome("Lazer")
                .descricao("Cinema, passeios, viagens")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .isGlobal(true)
                .build();

        BDDMockito.given(categoriaService.buscarPorId(2L)).willReturn(categoria);

        mockMvc.perform(get("/api/categorias/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lazer"))
                .andExpect(jsonPath("$.descricao").value("Cinema, passeios, viagens"));
    }

    @Test
    @DisplayName("Deve buscar categorias por tipo")
    @WithMockUser(username = "usuario@nucleo.com", roles = "USER")
    void deveBuscarCategoriasPorTipo() throws Exception {
        Categoria categoria = Categoria.builder()
                .id(3L)
                .nome("Salário")
                .descricao("Entradas de salário")
                .tipo(Categoria.TipoCategoria.ENTRADA)
                .isGlobal(true)
                .build();

        BDDMockito.given(categoriaService.buscarPorTipo(Categoria.TipoCategoria.ENTRADA)).willReturn(List.of(categoria));

        mockMvc.perform(get("/api/categorias/tipo/ENTRADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Salário"))
                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"));
    }

    @Test
    @DisplayName("Deve atualizar categoria (ADMIN)")
    @WithMockUser(username = "admin@nucleo.com", roles = "ADMIN")
    void deveAtualizarCategoria() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Educação", "Cursos e formações", Categoria.TipoCategoria.SAIDA);
        Categoria categoriaAtualizada = Categoria.builder()
                .id(4L)
                .nome("Educação")
                .descricao("Cursos e formações")
                .tipo(Categoria.TipoCategoria.SAIDA)
                .isGlobal(true)
                .build();

        BDDMockito.given(categoriaService.atualizar(4L, request)).willReturn(categoriaAtualizada);

        mockMvc.perform(put("/api/categorias/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Educação"))
                .andExpect(jsonPath("$.descricao").value("Cursos e formações"));
    }

    @Test
    @DisplayName("Deve deletar categoria (ADMIN)")
    @WithMockUser(username = "admin@nucleo.com", roles = "ADMIN")
    void deveDeletarCategoria() throws Exception {
        mockMvc.perform(delete("/api/categorias/5"))
                .andExpect(status().isNoContent());
    }
}
