package com.nucleo.model;

import com.nucleo.model.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "alertas")
public class Alerta extends BaseEntity {

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "nome_regra", length = 120, nullable = false)
    private String nomeRegra;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoAlerta tipo;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "conta_id")
    private Long contaId;

    @Column(name = "limite_valor", precision = 14, scale = 2)
    private BigDecimal limiteValor;

    @Column(name = "janela_dias")
    private Integer janelaDias;

    @Column(name = "notificar_email", nullable = false)
    private Boolean notificarEmail = true;

    public enum TipoAlerta {
        LIMITE_CATEGORIA,
        GASTO_ATIPICO,
        SALDO_MINIMO
    }

    // Getters e setters

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

    public TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlerta tipo) {
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

    public Boolean getNotificarEmail() {
        return notificarEmail;
    }

    public void setNotificarEmail(Boolean notificarEmail) {
        this.notificarEmail = notificarEmail;
    }
}
