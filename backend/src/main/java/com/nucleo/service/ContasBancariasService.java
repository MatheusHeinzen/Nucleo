package com.nucleo.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.ContasBancarias;
import com.nucleo.model.Usuario;
import com.nucleo.repository.ContasBancariasRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContasBancariasService {

    private final ContasBancariasRepository contasRepository;
    private final UsuarioService usuarioService;

    public ContasBancarias criar(ContasBancarias conta) throws Exception, EntityNotCreatedException,EntityNotFoundException {
        try{

        Usuario usuario = usuarioService.buscarEntidadePorId(SecurityUtils.getCurrentUserId());
        if (usuario == null) {
            throw new EntityNotFoundException("usuario.not-found");
        }
        conta.setUsuario(usuario);
        conta.setAtivo(true);
        return contasRepository.save(conta);
        }catch(Exception e){
            throw new EntityNotCreatedException("conta.not-criated");
        }
    }

    public List<ContasBancarias> listarPorUsuario(Long usuarioId) {
        try {

        return contasRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
        } catch (Exception e) {
            throw new EntityNotFoundException("conta.not-found");
        }
    }

    public List<ContasBancarias> listarTodas() {
        try {

        return contasRepository.findAll();
        }catch (Exception e) {
            throw new EntityNotFoundException("conta.not-found");
        }
    }

    public ContasBancarias buscarPorId(Long id) {
        if (SecurityUtils.isAdmin()) {
            return contasRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
        }
        return contasRepository.findByIdAndUsuarioIdAndAtivoTrue(id, SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada ou inativa."));
    }

    public ContasBancarias atualizar(Long id, ContasBancarias contaAtualizada) {
        try{
            ContasBancarias contaExistente = buscarPorId(id);

            contaExistente.setInstituicao(contaAtualizada.getInstituicao());
            contaExistente.setTipo(contaAtualizada.getTipo());
            contaExistente.setApelido(contaAtualizada.getApelido());
            contaExistente.setMoeda(contaAtualizada.getMoeda());
            contaExistente.setSaldoInicial(contaAtualizada.getSaldoInicial());

            return contasRepository.save(contaExistente);

        }catch (Exception e){
                throw new EntityNotUpdatedException("conta.not-updated");
            }
        }

    public void deletar(Long id, Long usuarioId, boolean isAdmin) {
        try{

        ContasBancarias conta;
        if (isAdmin) {
            conta = contasRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
        } else {
            conta = contasRepository.findByIdAndUsuarioId(id, usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));
        }

        conta.setAtivo(false);
        conta.setDeletadoEm(LocalDateTime.now());
        contasRepository.save(conta);
        }catch (Exception e){
            throw new EntityNotDeletedException("conta.not-deleted");
        }

    }
}