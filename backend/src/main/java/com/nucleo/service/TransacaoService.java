package com.nucleo.service;

import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.repository.TransacaoRepository;
import com.nucleo.service.generic.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService extends BaseService<Transacao, Long, TransacaoRepository> {


    public TransacaoService(TransacaoRepository repository) {
        super(repository);
    }

    public List<Transacao> findByUsuarioId(Long usuarioId) {
        return repository.findAllByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Page<Transacao> findByUsuarioId(Long usuarioId, Pageable pageable) {
        return repository.findAllByUsuarioIdAndAtivoTrue(usuarioId, pageable);
    }

    public List<Transacao> findByPeriodo(Long usuarioId, LocalDate inicio, LocalDate fim) {
        return repository.findAllByUsuarioIdAndDataBetweenAndAtivoTrue(usuarioId, inicio, fim);
    }

    public List<Transacao> findByCategoria(Long usuarioId, Categoria categoria) {
        return repository.findAllByUsuarioIdAndCategoriaAndAtivoTrue(usuarioId, categoria);
    }

    public List<Transacao> findByTipo(Long usuarioId, Transacao.TipoTransacao tipo) {
        return repository.findAllByUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
    }

    public BigDecimal getTotalEntradas(Long usuarioId) {
        return repository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
    }

    public BigDecimal getTotalSaidas(Long usuarioId) {
        return repository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.SAIDA);
    }

    public BigDecimal getSaldo(Long usuarioId) {
        BigDecimal entradas = getTotalEntradas(usuarioId);
        BigDecimal saidas = getTotalSaidas(usuarioId);
        return entradas.subtract(saidas);
    }
}