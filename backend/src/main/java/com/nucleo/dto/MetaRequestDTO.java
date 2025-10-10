package com.nucleo.dto;

import com.nucleo.model.StatusMeta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MetaRequestDTO {
    private String titulo;
    private BigDecimal valorAlvo;
    private LocalDate dataLimite;
    private Long categoriaId; // opcional
    private StatusMeta status = StatusMeta.ativa;
}
