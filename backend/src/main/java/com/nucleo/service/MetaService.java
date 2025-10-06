package com.nucleo.service;

import com.nucleo.model.Meta;
import com.nucleo.repository.MetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class MetaService {

    @Autowired
    private MetaRepository metaRepository;

    public Meta criar(Meta meta) {
        // TODO: Atribuir o ID do usuário LOGADO antes de salvar
        return metaRepository.save(meta);
    }

    public List<Meta> listarPorUsuario(Long usuarioId) {
        return metaRepository.findByUsuarioId(usuarioId);
    }



    public Meta buscarPorId(Long id, Long usuarioId) {
        // Busca a meta e verifica se ela pertence ao usuário que fez a requisição
        return metaRepository.findById(id)
                .filter(meta -> meta.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada ou não pertence a este usuário."));
    }

    public Meta atualizar(Long id, Meta metaAtualizada, Long usuarioId) {
        Meta metaExistente = buscarPorId(id, usuarioId);

        // Atualiza os campos que podem ser modificados
        metaExistente.setTitulo(metaAtualizada.getTitulo());
        metaExistente.setValorAlvo(metaAtualizada.getValorAlvo());
        metaExistente.setDataLimite(metaAtualizada.getDataLimite());
        metaExistente.setCategoriaId(metaAtualizada.getCategoriaId());
        metaExistente.setStatus(metaAtualizada.getStatus());

        return metaRepository.save(metaExistente);
    }

    // Para o DELETE, podemos optar por remover a meta do banco ou cancelá-la.
    // Cancelar é geralmente uma abordagem melhor para manter o histórico.
    public void cancelar(Long id, Long usuarioId) {
        Meta meta = buscarPorId(id, usuarioId);
        meta.setStatus(com.matheus.Nucleo.model.StatusMeta.cancelada);
        metaRepository.save(meta);
    }
}
