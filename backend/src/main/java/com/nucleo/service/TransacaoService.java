package com.nucleo.service;

import com.nucleo.dto.TransacaoRequestDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.model.Usuario;
import com.nucleo.repository.TransacaoRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.utils.EntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    public Transacao criar(TransacaoRequestDTO transacao) throws EntityNotCreatedException {
        try {
            Usuario usuario = usuarioService.getUsuarioById(usuarioService.getUsuarioIdLogado());
            Categoria categoria = categoriaService.buscarPorId(transacao.getCategoriaId());

            if (usuario == null) throw new AuthenticationException("error.auth");
            if (categoria == null) throw new EntityNotFoundException("categoria.not-found");

            Transacao nova = Transacao.builder()
                    .descricao(transacao.getDescricao())
                    .valor(transacao.getValor())
                    .data(transacao.getData())
                    .tipo(transacao.getTipo())
                    .categoria(categoria)
                    .usuario(usuario)
                    .build();

            return transacaoRepository.save(nova);
        } catch (Exception e) {
            throw new EntityNotCreatedException("transacao.not-created");
        }
    }

    public List<Transacao> findByUsuarioId() {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        return transacaoRepository.findAllByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public BigDecimal getTotalEntradas(Long usuarioId) {
        return transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
    }

    public BigDecimal getTotalSaidas(Long usuarioId) {
        return transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.SAIDA);
    }

    public BigDecimal getSaldo(Long usuarioId) {
        BigDecimal entradas = getTotalEntradas(usuarioId);
        BigDecimal saidas = getTotalSaidas(usuarioId);
        return entradas.subtract(saidas);
    }

    public Transacao encontraPorId(Long id) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        return transacaoRepository.findTransacaoByUsuario_IdAndId(usuarioId, id);
    }

    public Transacao atualizar(Long id, TransacaoRequestDTO transacao) {
        try {
            Transacao existente = encontraPorId(id);
            if (existente == null) throw new EntityNotFoundException("transacao.not-found");

            Categoria categoria = categoriaService.buscarPorId(transacao.getCategoriaId());
            Usuario usuario = usuarioService.getUsuarioById(usuarioService.getUsuarioIdLogado());

            EntityUtils.atualizarSeDiferente(existente::setDescricao, transacao.getDescricao(), existente.getDescricao());
            EntityUtils.atualizarSeDiferente(existente::setValor, transacao.getValor(), existente.getValor());
            EntityUtils.atualizarSeDiferente(existente::setData, transacao.getData(), existente.getData());
            EntityUtils.atualizarSeDiferente(existente::setTipo, transacao.getTipo(), existente.getTipo());
            EntityUtils.atualizarSeDiferente(existente::setCategoria, categoria, existente.getCategoria());
            EntityUtils.atualizarSeDiferente(existente::setUsuario, usuario, existente.getUsuario());

            return transacaoRepository.save(existente);
        } catch (Exception e) {
            throw new EntityNotUpdatedException("transacao.not-updated");
        }
    }

    public void excluir(Long id) {
        Transacao t = encontraPorId(id);
        if (t == null) throw new EntityNotFoundException("transacao.not-found");
        try {
            transacaoRepository.deleteById(t.getId());
        } catch (Exception e) {
            throw new EntityNotDeletedException("transacao.not-deleted");
        }
    }

    public List<Transacao> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        return transacaoRepository.findByUsuarioAndPeriodo(usuarioId, dataInicio, dataFim);
    }

    public List<Transacao> encontraPorTipo(Transacao.TipoTransacao tipo) {
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        return transacaoRepository.findAllByUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
    }

    public List<Transacao> encontraPorCategoria(Long categoriaId) {
        Categoria categoria = categoriaService.buscarPorId(categoriaId);
        Long usuarioId = usuarioService.getUsuarioIdLogado();
        return transacaoRepository.findAllByUsuarioIdAndCategoriaIdAndAtivoTrue(usuarioId, categoria.getId());
    }
}
