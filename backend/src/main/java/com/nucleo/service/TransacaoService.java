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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.nucleo.security.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;


    public Transacao criar(TransacaoRequestDTO transacao) throws EntityNotCreatedException {
        try{
            Long usuarioId = getCurrentUserId();
            Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
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
            return transacaoRepository.findAllByUsuarioIdAndAtivoTrue(getCurrentUserId());
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public List<Transacao> listarTodas() throws EntityNotFoundException {
        try {
            return transacaoRepository.findByUsuarioId(getCurrentUserId());
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }


    public List<Transacao> listarTodas(Long id) throws EntityNotFoundException {

        try {
            return transacaoRepository.findByUsuarioId(id);
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
            return transacaoRepository.sumValorByUsuarioIdAndTipo(usuarioId, Transacao.TipoTransacao.SAIDA);
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

    public Transacao buscarPorIdEUsuario(Long id, Long usuarioId) throws EntityNotFoundException {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("transacao.not-found"));
        
        if (!SecurityUtils.isAdmin() && !transacao.getUsuario().getId().equals(usuarioId)) {
            throw new EntityNotFoundException("Transação não encontrada ou não pertence a este usuário.");
        }
        
        return transacao;
    }

    public Transacao atualizar(Long id, TransacaoRequestDTO transacao) throws EntityNotUpdatedException {
        try {
            Long usuarioId = getCurrentUserId();
            Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
            if(usuario == null){
                throw new EntityNotFoundException("usuario.not-found");
            }

            Transacao transacaoExistente = buscarPorIdEUsuario(id, usuario.getId());
            if(transacaoExistente == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }

            Categoria categoria = categoriaService.buscarPorId (transacao.getCategoriaId());
            if(categoria == null){
                throw new EntityNotFoundException("categoria.not-found");
            }

            EntityUtils.atualizarSeDiferente(transacaoExistente::setDescricao,transacao.getDescricao(),transacaoExistente.getDescricao());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setValor,transacao.getValor(),transacaoExistente.getValor());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setData,transacao.getData(),transacaoExistente.getData());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setTipo,transacao.getTipo(),transacaoExistente.getTipo());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setCategoria,categoria,transacaoExistente.getCategoria());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setUsuario,usuario,transacaoExistente.getUsuario());


            return transacaoRepository.save(transacaoExistente);
        } catch (Exception e) {
           throw new EntityNotUpdatedException("transacao.not-updated");
        }
    }

    public void excluir(Long id) throws EntityNotFoundException, EntityNotDeletedException {
        try {
            Long usuarioId = getCurrentUserId();
            Transacao t = buscarPorIdEUsuario(id, usuarioId);
            if(t == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }
            transacaoRepository.deleteById(t.getId());
        }catch(Exception e){
            throw new EntityNotDeletedException("transacao.not-deleted");
        }

    }

    public List<Transacao> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Long usuarioId = getCurrentUserId();
        return transacaoRepository.findByUsuarioAndPeriodo(usuarioId, dataInicio, dataFim);
    }


    public List<Transacao> encontraPorTipo(Transacao.TipoTransacao tipo) throws EntityNotFoundException {
        try {
            Long usuarioId = getCurrentUserId();
            return transacaoRepository.findAllByUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
        } catch (Exception e) {
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

    public List<Transacao> encontraPorCategoria( Long categoriaId) throws EntityNotFoundException {
        try{
            Categoria cat = categoriaService.buscarPorId(categoriaId);
            if(cat == null){
                throw new EntityNotFoundException("categoria.not-found");
            }
            return transacaoRepository.findAllByUsuarioIdAndCategoriaIdAndAtivoTrue(getCurrentUserId(),cat.getId());
        }catch(Exception e){
            throw new EntityNotFoundException("transacao.not-found");
        }
    }

}