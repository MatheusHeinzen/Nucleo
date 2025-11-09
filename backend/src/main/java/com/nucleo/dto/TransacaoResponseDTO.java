package com.nucleo.dto;

import com.nucleo.model.Transacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransacaoResponseDTO {
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
    private Long contaId;

    public static TransacaoResponseDTO fromEntity(Transacao transacao) {
        TransacaoResponseDTO response = new TransacaoResponseDTO();
        response.setId(transacao.getId());
        response.setDescricao(transacao.getDescricao());
        response.setValor(transacao.getValor());
        response.setData(transacao.getData());
        response.setTipo(transacao.getTipo());
        response.setCategoriaId(transacao.getCategoria() != null ? transacao.getCategoria().getId() : null);
        response.setCategoriaNome(transacao.getCategoria() != null ? transacao.getCategoria().getNome() : null);
        response.setDataCriacao(transacao.getDataCriacao());
        response.setDataAtualizacao(transacao.getDataAtualizacao());
        response.setUsuarioId(transacao.getUsuario().getId());
        response.setUsuarioNome(transacao.getUsuario().getNome());
        response.setContaId(transacao.getConta() != null ? transacao.getConta().getId() : null);
        return response;
    }
}