package com.nucleo.dto;

import com.nucleo.model.Alerta;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertaRequest {

    @NotBlank(message = "Nome da regra é obrigatório")
    @Size(min = 3, max = 120, message = "Nome da regra deve ter entre 3 e 120 caracteres")
    private String nomeRegra;

    @NotNull(message = "Tipo é obrigatório")
    private Alerta.TipoAlerta tipo;

    private Long categoriaId;

    private Long contaId;

    @DecimalMin(value = "0.0", message = "Limite valor não pode ser negativo")
    private BigDecimal limiteValor;

    @Min(value = 1, message = "Janela de dias deve ser pelo menos 1")
    @Max(value = 365, message = "Janela de dias não pode exceder 365")
    private Integer janelaDias;

    private Boolean ativo;
    private Boolean notificarEmail;
}
