package com.nucleo.service;

import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.repository.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private TransacaoRepository transacaoRepository;

    public List<Transacao> findByUsuarioId(Long usuarioId) throws EntityNotFoundException {
        try {
            return repository.findAllByUsuarioIdAndAtivoTrue(usuarioId);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public BigDecimal getTotalEntradas(Long usuarioId) throws EntityNotFoundException {
        try {
            return repository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }

    }

    public BigDecimal getTotalSaidas(Long usuarioId) throws EntityNotFoundException {
        try {
            return repository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public BigDecimal getSaldo(Long usuarioId) {
        BigDecimal entradas = getTotalEntradas(usuarioId);
        BigDecimal saidas = getTotalSaidas(usuarioId);
        return entradas.subtract(saidas);
    }

    public Transacao atualizar(Long id, Transacao transacao, Long usuarioId) throws EntityNotUpdatedException {
        try{

        Transacao transacaoAntiga = transacaoRepository.findTransacaoByUsuario_IdAndId(usuarioId, id);

        atualizarSeDiferente(transacaoAntiga::setDescricao, transacao.getDescricao(), transacaoAntiga.getDescricao());
        atualizarSeDiferente(transacaoAntiga::setCategoria, transacao.getCategoria(), transacaoAntiga.getCategoria());
        atualizarSeDiferente(transacaoAntiga::setTipo, transacao.getTipo(), transacaoAntiga.getTipo());
        atualizarSeDiferente(transacaoAntiga::setValor, transacao.getValor(), transacaoAntiga.getValor());

        return transacaoRepository.save(transacaoAntiga);
        }catch(Exception e){
            throw new EntityNotUpdatedException("transacao.not-updated");
        }

    }
}