package com.nucleo.dto;

import com.nucleo.model.Alerta;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class AlertaRequest {

    @NotNull(message = "Usuário é obrigatório")
    private Long usuarioId;

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

    // opcionais: se vierem nulos, usamos defaults na entidade / serviço
    private Boolean ativo;
    private Boolean notificarEmail;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeRegra() {
        return nomeRegra;
    }

    public void setNomeRegra(String nomeRegra) {
        this.nomeRegra = nomeRegra;
    }

    public Alerta.TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(Alerta.TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public BigDecimal getLimiteValor() {
        return limiteValor;
    }

    public void setLimiteValor(BigDecimal limiteValor) {
        this.limiteValor = limiteValor;
    }

    public Integer getJanelaDias() {
        return janelaDias;
    }

    public void setJanelaDias(Integer janelaDias) {
        this.janelaDias = janelaDias;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getNotificarEmail() {
        return notificarEmail;
    }

    public void setNotificarEmail(Boolean notificarEmail) {
        this.notificarEmail = notificarEmail;
    }
}
