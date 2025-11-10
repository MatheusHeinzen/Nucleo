package com.nucleo.dto;

import com.nucleo.model.Alerta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AlertaResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private String nomeRegra;
    private Alerta.TipoAlerta tipo;
    private Long categoriaId;
    private String categoriaNome;
    private Long contaId;
    private String contaApelido;
    private BigDecimal limiteValor;
    private Integer janelaDias;
    private Boolean ativo;
    private Boolean notificarEmail;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static AlertaResponse fromEntity(Alerta alerta) {
        AlertaResponse response = new AlertaResponse();
        response.setId(alerta.getId());
        response.setUsuarioId(alerta.getUsuario() != null ? alerta.getUsuario().getId() : null);
        response.setUsuarioNome(alerta.getUsuario() != null ? alerta.getUsuario().getNome() : null);
        response.setNomeRegra(alerta.getNomeRegra());
        response.setTipo(alerta.getTipo());
        response.setCategoriaId(alerta.getCategoria() != null ? alerta.getCategoria().getId() : null);
        response.setCategoriaNome(alerta.getCategoria() != null ? alerta.getCategoria().getNome() : null);
        response.setContaId(alerta.getConta() != null ? alerta.getConta().getId() : null);
        response.setContaApelido(alerta.getConta() != null ? alerta.getConta().getApelido() : null);
        response.setLimiteValor(alerta.getLimiteValor());
        response.setJanelaDias(alerta.getJanelaDias());
        response.setAtivo(alerta.getAtivo());
        response.setNotificarEmail(alerta.getNotificarEmail());
        response.setDataCriacao(alerta.getDataCriacao());
        response.setDataAtualizacao(alerta.getDataAtualizacao());
        return response;
    }
}
