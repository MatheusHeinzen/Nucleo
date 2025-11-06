package com.nucleo.service;

import com.nucleo.model.ContasBancarias;
import com.nucleo.repository.ContasBancariasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContasBancariasService {

    @Autowired
    private ContasBancariasRepository contasRepository;

    public ContasBancarias criar(ContasBancarias conta) {
        // TODO: Atribuir o ID do usuário LOGADO
        // conta.setUsuarioId(idDoUsuarioLogado);
        conta.setAtivo(true); // Garante que a conta nova seja ativa
        return contasRepository.save(conta);
    }

    public List<ContasBancarias> listarPorUsuario(Long usuarioId) {
        return contasRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public ContasBancarias buscarPorId(Long id, Long usuarioId) {
        return contasRepository.findByIdAndUsuarioIdAndAtivoTrue(id, usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada ou inativa."));
    }

    public ContasBancarias atualizar(Long id, ContasBancarias contaAtualizada, Long usuarioId) {
        ContasBancarias contaExistente = buscarPorId(id, usuarioId); // Já valida se está ativa e pertence ao usuário

        // Atualiza os campos permitidos
        contaExistente.setInstituicao(contaAtualizada.getInstituicao());
        contaExistente.setTipo(contaAtualizada.getTipo());
        contaExistente.setApelido(contaAtualizada.getApelido());
        contaExistente.setMoeda(contaAtualizada.getMoeda());
        // Saldo inicial geralmente não é atualizado, mas sim via transações.
        // Mas se for permitido, adicione: contaExistente.setSaldoInicial(contaAtualizada.getSaldoInicial());

        return contasRepository.save(contaExistente);
    }

    public void deletar(Long id, Long usuarioId) {
        // Aqui, buscamos a conta (mesmo que já inativa, para evitar erro 404 se deletarem 2x)
        ContasBancarias conta = contasRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));

        conta.setAtivo(false);
        conta.setDeletadoEm(LocalDateTime.now());
        contasRepository.save(conta);
    }
}