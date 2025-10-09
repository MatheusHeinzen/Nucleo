package com.nucleo.dto;

import com.nucleo.model.Transacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoRequestDTO {
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private Transacao.TipoTransacao tipo;
    private Long categoriaId;
    private Long usuarioId;
}