package com.nucleo.dto;

import com.nucleo.model.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Retorno de categoria cadastrada")
public record CategoriaResponseDTO(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Transporte")
        String nome,

        @Schema(example = "Gastos com deslocamento e combustível")
        String descricao,

        @Schema(example = "SAIDA")
        Categoria.TipoCategoria tipo,

        @Schema(example = "false", description = "Indica se a categoria é global (padrão do sistema)")
        Boolean isGlobal,

        @Schema(example = "5", description = "ID do usuário dono da categoria (null se for global)")
        Long usuarioId,

        @Schema(example = "Isabel Pontes", description = "Nome do usuário dono da categoria (null se for global)")
        String usuarioNome
) {

    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .tipo(categoria.getTipo())
                .isGlobal(categoria.getIsGlobal())
                .usuarioId(categoria.getUsuario() != null ? categoria.getUsuario().getId() : null)
                .usuarioNome(categoria.getUsuario() != null ? categoria.getUsuario().getNome() : null)
                .build();
    }
}
