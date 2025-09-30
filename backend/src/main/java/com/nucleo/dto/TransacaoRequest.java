// TransacaoRequest.java
package com.nucleo.dto;

import com.nucleo.model.Transacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransacaoRequest {
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private Transacao.TipoTransacao tipo;
    private Transacao.Categoria categoria;
    private Long usuarioId;
}