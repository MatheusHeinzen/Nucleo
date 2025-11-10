package com.nucleo.dto;

import com.nucleo.model.Alerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AlertaResponse {

    private Long id;
    private Long usuarioId;
    private String nomeRegra;
    private Alerta.TipoAlerta tipo;
    private Long categoriaId;
    private Long contaId;
    private BigDecimal limiteValor;
    private Integer janelaDias;
    private Boolean ativo;
    private Boolean notificarEmail;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public AlertaResponse() {
    }

    public AlertaResponse(Alerta alerta) {
        this.id = alerta.getId();
        this.usuarioId = alerta.getUsuarioId();
        this.nomeRegra = alerta.getNomeRegra();
        this.tipo = alerta.getTipo();
        this.categoriaId = alerta.getCategoriaId();
        this.contaId = alerta.getContaId();
        this.limiteValor = alerta.getLimiteValor();
        this.janelaDias = alerta.getJanelaDias();
        this.ativo = alerta.getAtivo();
        this.notificarEmail = alerta.getNotificarEmail();
        this.dataCriacao = alerta.getDataCriacao();
        this.dataAtualizacao = alerta.getDataAtualizacao();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNomeRegra() {
        return nomeRegra;
    }

    public Alerta.TipoAlerta getTipo() {
        return tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public Long getContaId() {
        return contaId;
    }

    public BigDecimal getLimiteValor() {
        return limiteValor;
    }

    public Integer getJanelaDias() {
        return janelaDias;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Boolean getNotificarEmail() {
        return notificarEmail;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setNomeRegra(String nomeRegra) {
        this.nomeRegra = nomeRegra;
    }

    public void setTipo(Alerta.TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public void setLimiteValor(BigDecimal limiteValor) {
        this.limiteValor = limiteValor;
    }

    public void setJanelaDias(Integer janelaDias) {
        this.janelaDias = janelaDias;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void setNotificarEmail(Boolean notificarEmail) {
        this.notificarEmail = notificarEmail;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
