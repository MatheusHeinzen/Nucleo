package com.nucleo.dto;

import com.nucleo.model.Transacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


public record TransacaoRequestDTO (
    String descricao,
     BigDecimal valor,
     LocalDate data,
     Transacao.TipoTransacao tipo,
     Long categoriaId,
     Long contaId
){}