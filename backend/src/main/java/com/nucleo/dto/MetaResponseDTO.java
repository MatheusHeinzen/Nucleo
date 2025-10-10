package com.nucleo.dto;

import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MetaResponseDTO {
    private Long id;
    private String titulo;
    private BigDecimal valorAlvo;
    private LocalDate dataLimite;
    private Long categoriaId;
    private StatusMeta status;
    private Long usuarioId;

    public static MetaResponseDTO fromEntity(Meta meta) {
        return MetaResponseDTO.builder()
                .id(meta.getId())
                .titulo(meta.getTitulo())
                .valorAlvo(meta.getValorAlvo())
                .dataLimite(meta.getDataLimite())
                .categoriaId(meta.getCategoriaId())
                .status(meta.getStatus())
                .usuarioId(meta.getUsuarioId())
                .build();
    }
}
