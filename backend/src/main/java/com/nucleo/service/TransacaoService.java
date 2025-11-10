package com.nucleo.service;

import com.nucleo.dto.TransacaoRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.*;
import com.nucleo.repository.ContasBancariasRepository;
import com.nucleo.repository.TransacaoRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.utils.EntityUtils;
import com.nucleo.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nucleo.security.SecurityUtils.getCurrentUserId;    import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final AlertaService alertaService;
    private final EmailService emailService; // ✅ injetado
    private final ContasBancariasRepository contasBancariasRepository;




    @Transactional
    public Transacao criar(TransacaoRequestDTO transacao) throws EntityNotCreatedException {
        try {
            Long usuarioId = getCurrentUserId();
            Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
            Categoria categoria = categoriaService.buscarPorId(transacao.categoriaId());
            ContasBancarias c = contasBancariasRepository.findByIdAndUsuarioIdAndAtivoTrue(transacao.contaId(),usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

            System.out.println("passou 1");
            Transacao transacaoNova = Transacao.builder()
                    .descricao(transacao.descricao())
                    .valor(transacao.valor())
                    .data(transacao.data())
                    .tipo(transacao.tipo())
                    .categoria(categoria)
                    .conta(c)
                    .usuario(usuario)
                    .build();

            System.out.println("valor");
            System.out.println(transacaoNova.getValor());

            System.out.println("passou 2");
            List<Transacao> transacoesUltimoMes =
                    transacaoRepository.findByUsuarioAndPeriodo(usuarioId, LocalDate.now().minusDays(30), LocalDate.now()).stream().filter(t -> t.getTipo() == Transacao.TipoTransacao.SAIDA).toList();

            List<Transacao> transacaosCategoria = transacoesUltimoMes.stream()
                    .filter(t -> t.getCategoria().equals(categoria))
                    .toList();


            BigDecimal gastosUltimoMes = transacaosCategoria.stream()
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);



            System.out.println("passou 4");
            List<Alerta> alertas = alertaService.listarPorUsuario(usuarioId);
            for (Alerta alert : alertas) {

                if(alert.getTipo() == Alerta.TipoAlerta.LIMITE_CATEGORIA) {
                    System.out.println(gastosUltimoMes);
                        if (gastosUltimoMes.compareTo(alert.getLimiteValor()) > 0) {
                            emailService.enviarEmail(
                                    "joaogotado@gmail.com",
                                    "⚠️ Alerta de Gastos - Núcleo Financeiro",
                                    "Olá, " + usuario.getNome() +
                                            "! Você ultrapassou seu limite de gastos de R$" + alert.getLimiteValor() +
                                            " no último mês. Revise suas despesas para manter o equilíbrio financeiro 💰"
                            );

                            System.out.println("enviou email");
                        }

                } else if (alert.getTipo() == Alerta.TipoAlerta.SALDO_MINIMO) {

                    if(getSaldo(getCurrentUserId()).compareTo(alert.getLimiteValor())<=0) {
                        emailService.enviarEmail(
                                "joaogotado@gmail.com",
                                "⚠️ Alerta de Gastos - Núcleo Financeiro",
                                "Olá, " + usuario.getNome() +
                                        "! Você esta abaixo do limite de saldo de R$" + alert.getLimiteValor() +". " +
                                        " Revise suas despesas para manter o equilíbrio financeiro 💰"
                        );

                        System.out.println("enviou email");
                    }
                }
            }

            return transacaoRepository.save(transacaoNova);

        } catch (Exception e) {
            throw new EntityNotCreatedException("transacao.not-created");
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
            UsuarioResponseDTO u= usuarioService.buscarPorId(id);
            if(u == null){
                throw new EntityNotFoundException("usuario.not-found");
            }
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

    public Transacao buscarPorIdEUsuario(Long id) throws EntityNotFoundException {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("transacao.not-found"));
        
        if (!SecurityUtils.isAdmin() && !transacao.getUsuario().getId().equals(getCurrentUserId())) {
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

            Transacao transacaoExistente = buscarPorIdEUsuario(id);
            if(transacaoExistente == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }

            Categoria categoria = categoriaService.buscarPorId (transacao.categoriaId());
            if(categoria == null){
                throw new EntityNotFoundException("categoria.not-found");
            }

            EntityUtils.atualizarSeDiferente(transacaoExistente::setDescricao,transacao.descricao(),transacaoExistente.getDescricao());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setValor,transacao.valor(),transacaoExistente.getValor());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setData,transacao.data(),transacaoExistente.getData());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setTipo,transacao.tipo(),transacaoExistente.getTipo());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setCategoria,categoria,transacaoExistente.getCategoria());
            EntityUtils.atualizarSeDiferente(transacaoExistente::setUsuario,usuario,transacaoExistente.getUsuario());


            return transacaoRepository.save(transacaoExistente);
        } catch (Exception e) {
           throw new EntityNotUpdatedException("transacao.not-updated");
        }
    }

    public void excluir(Long id) throws EntityNotFoundException, EntityNotDeletedException {
        try {
            Transacao t = buscarPorIdEUsuario(id);

            if(t == null) {
                throw new EntityNotFoundException("transacao.not-found");
            }
            if(!t.getUsuario().getId().equals(getCurrentUserId()) && !SecurityUtils.isAdmin()) {
                throw new AuthenticationException("transacao.not-deleted");
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