package com.nucleo.service;

import com.nucleo.model.ContasBancarias;
import com.nucleo.model.Usuario;
import com.nucleo.repository.ContasBancariasRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContasBancariasService {

    private final ContasBancariasRepository contasRepository;
    private final UsuarioService usuarioService;

    public ContasBancarias criar(ContasBancarias conta, Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        conta.setUsuario(usuario);
        conta.setAtivo(true);
        return contasRepository.save(conta);
    }

    public List<ContasBancarias> listarPorUsuario(Long usuarioId) {
        return contasRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public List<ContasBancarias> listarTodas() {
        return contasRepository.findAll();
    }

    public ContasBancarias buscarPorId(Long id, Long usuarioId) {
        return contasRepository.findByIdAndUsuarioIdAndAtivoTrue(id, usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada ou inativa."));
    }

    public ContasBancarias atualizar(Long id, ContasBancarias contaAtualizada, Long usuarioId) {
        ContasBancarias contaExistente = buscarPorId(id, usuarioId);

        contaExistente.setInstituicao(contaAtualizada.getInstituicao());
        contaExistente.setTipo(contaAtualizada.getTipo());
        contaExistente.setApelido(contaAtualizada.getApelido());
        contaExistente.setMoeda(contaAtualizada.getMoeda());
        contaExistente.setSaldoInicial(contaAtualizada.getSaldoInicial());

        return contasRepository.save(contaExistente);
    }

    public void deletar(Long id, Long usuarioId) {
        ContasBancarias conta = contasRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada."));

        conta.setAtivo(false);
        conta.setDeletadoEm(LocalDateTime.now());
        contasRepository.save(conta);
    }
}