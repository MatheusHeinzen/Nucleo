package com.nucleo.dto;

import com.nucleo.model.Transacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransacaoResponse {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private Transacao.TipoTransacao tipo;
    private Long categoriaId;
    private String categoriaNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long usuarioId;
    private String usuarioNome;

    public static TransacaoResponse fromEntity(Transacao transacao) {
        TransacaoResponse response = new TransacaoResponse();
        response.setId(transacao.getId());
        response.setDescricao(transacao.getDescricao());
        response.setValor(transacao.getValor());
        response.setData(transacao.getData());
        response.setTipo(transacao.getTipo());
        response.setCategoriaId(transacao.getCategoria().getId());
        response.setCategoriaNome(transacao.getCategoria().getNome());
        response.setDataCriacao(transacao.getDataCriacao());
        response.setDataAtualizacao(transacao.getDataAtualizacao());
        response.setUsuarioId(transacao.getUsuario().getId());
        response.setUsuarioNome(transacao.getUsuario().getNome());
        return response;
    }
}