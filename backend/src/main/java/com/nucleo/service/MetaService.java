package com.nucleo.service;

import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import com.nucleo.repository.MetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;


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



        atualizarSeDiferente(metaExistente::setTitulo, metaAtualizada.getTitulo(), metaExistente.getTitulo());
        atualizarSeDiferente(metaExistente::setValorAlvo, metaAtualizada.getValorAlvo(), metaExistente.getValorAlvo());
        atualizarSeDiferente(metaExistente::setDataLimite, metaAtualizada.getDataLimite(), metaExistente.getDataLimite());
        atualizarSeDiferente(metaExistente::setCategoriaId, metaAtualizada.getCategoriaId(), metaExistente.getCategoriaId());
        atualizarSeDiferente(metaExistente::setStatus, metaAtualizada.getStatus(), metaExistente.getStatus());

        return metaRepository.save(metaExistente);
    }

    public void cancelar(Long id, Long usuarioId) {
        Meta meta = buscarPorId(id, usuarioId);
        meta.setStatus(StatusMeta.cancelada);
        metaRepository.save(meta);
    }
}
