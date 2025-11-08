package com.nucleo.dto;

import com.nucleo.model.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Dados para criação ou atualização de categoria")
public record CategoriaRequestDTO(

        @Schema(example = "Transporte")
        String nome,

        @Schema(example = "Gastos com deslocamento e combustível")
        String descricao,

        @Schema(example = "SAIDA", description = "Tipo da categoria (ENTRADA ou SAIDA)")
        Categoria.TipoCategoria tipo
) {}
