package com.nucleo.dto;

import com.nucleo.model.Categoria;
import jdk.jfr.Description;
import lombok.Data;

@Data
public class CategoriaRequestDTO {
    private String nome;
    private String descricao;
    @Description("enum com os tipos:{ENTRADA,SAIDA}")
    private Categoria.TipoCategoria tipo;
}
