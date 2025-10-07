package com.nucleo.service;

import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
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
        return metaRepository.save(meta);
    }

    public List<Meta> listarPorUsuario(Long usuarioId) {
        return metaRepository.findByUsuarioId(usuarioId);
    }



    public Meta buscarPorId(Long id, Long usuarioId) {
        return metaRepository.findById(id)
                .filter(meta -> meta.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada ou não pertence a este usuário."));
    }

    public Meta atualizar(Long id, Meta metaAtualizada, Long usuarioId) {
        Meta metaExistente = buscarPorId(id, usuarioId);

        metaExistente.setTitulo(metaAtualizada.getTitulo());
        metaExistente.setValorAlvo(metaAtualizada.getValorAlvo());
        metaExistente.setDataLimite(metaAtualizada.getDataLimite());
        metaExistente.setCategoriaId(metaAtualizada.getCategoriaId());
        metaExistente.setStatus(metaAtualizada.getStatus());

        return metaRepository.save(metaExistente);
    }

    public void cancelar(Long id, Long usuarioId) {
        Meta meta = buscarPorId(id, usuarioId);
        meta.setStatus(StatusMeta.cancelada);
        metaRepository.save(meta);
    }
}
