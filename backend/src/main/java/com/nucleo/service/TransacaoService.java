package com.nucleo.service;

import com.nucleo.dto.TransacaoRequestDTO;
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
        try{

            Usuario usuario = usuarioService.buscarUsuarioPorEmail(SecurityUtils.getCurrentUserEmail());
            Categoria categoria = categoriaService.buscarPorId(transacao.getCategoriaId());
            Transacao transacaoNova = Transacao.builder()
                    .descricao(transacao.getDescricao())
                    .valor(transacao.getValor())
                    .data(transacao.getData())
                    .tipo(transacao.getTipo())
                    .categoria(categoria)
                    .usuario(usuario)
                    .build();

            transacaoRepository.save(transacaoNova);
            return transacaoNova;
        }catch(Exception e){
            throw new EntityNotCreatedException("transacao.not-created");
        }
    }

    public List<Transacao> findByUsuarioId() throws EntityNotFoundException {
        try {
            Long usuarioId = SecurityUtils.getCurrentUserId();
            return transacaoRepository.findAllByUsuarioIdAndAtivoTrue(usuarioId);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public BigDecimal getTotalEntradas(Long usuarioId) throws EntityNotFoundException {
        try {
            return transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }

    }

    public BigDecimal getTotalSaidas(Long usuarioId) throws EntityNotFoundException {
        try {
            return transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.ENTRADA);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public BigDecimal getSaldo(Long usuarioId) {
        BigDecimal entradas = getTotalEntradas(usuarioId);
        BigDecimal saidas = getTotalSaidas(usuarioId);
        return entradas.subtract(saidas);
    }

    public Transacao encontraPorId(Long id) throws EntityNotFoundException{
            return transacaoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("transacao.not-found"));
    }

    public Transacao atualizar(Long id, TransacaoRequestDTO transacao) throws EntityNotUpdatedException {
        try {

            Usuario usuario = usuarioService.buscarUsuarioPorEmail(SecurityUtils.getCurrentUserEmail());
            if(usuario == null){
                throw new EntityNotFoundException("usuario.not-found");
            }


            Categoria categoria = categoriaService.buscarPorId (transacao.getCategoriaId());
            if(categoria == null){
                throw new EntityNotFoundException("categoria.not-found");
            }

            var transacaoExistente = encontraPorId(id);
            if(transacaoExistente == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }

            EntityUtils.atualizarSeDiferente(transacaoExistente::setDescricao,transacao.getDescricao(),transacaoExistente.getDescricao());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setValor,transacao.getValor(),transacaoExistente.getValor());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setData,transacao.getData(),transacaoExistente.getData());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setTipo,transacao.getTipo(),transacaoExistente.getTipo());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setCategoria,categoria,transacaoExistente.getCategoria());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setUsuario,usuario,transacaoExistente.getUsuario());


            return transacaoExistente;
        } catch (Exception e) {
           throw new EntityNotUpdatedException("transacao.not-updated");
        }
    }

    public void excluir(Long id) throws EntityNotFoundException, EntityNotDeletedException {
        try {
            Transacao t = encontraPorId(id);
            if(t == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }
            transacaoRepository.deleteById(t.getId());
        }catch(Exception e){
            throw new EntityNotDeletedException("transacao.not-deleted");
        }

    }

    public List<Transacao> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Long usuarioId = usuarioService.buscarUsuarioPorEmail(SecurityUtils.getCurrentUserEmail()).getId();
        return transacaoRepository.findByUsuarioAndPeriodo(usuarioId, dataInicio, dataFim);
    }


    public List<Transacao> encontraPorTipo( Transacao.TipoTransacao tipo) throws EntityNotFoundException {
        return null;
    }

    public List<Transacao> encontraPorCategoria( Long categoriaId) throws EntityNotFoundException {
        try{
            Categoria cat = categoriaService.buscarPorId(categoriaId);
            if(cat == null){
                throw new EntityNotFoundException("categoria.not-found");
            }
            return transacaoRepository.findAllByUsuarioIdAndCategoriaIdAndAtivoTrue(SecurityUtils.getCurrentUserId(),cat.getId());
        }catch(Exception e){
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

}