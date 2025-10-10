package com.nucleo.dto;

import com.nucleo.model.Categoria;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Categoria.TipoCategoria tipo;
    private Boolean ativo;

    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .tipo(categoria.getTipo())
                .ativo(categoria.getAtivo())
                .build();
    }
}
