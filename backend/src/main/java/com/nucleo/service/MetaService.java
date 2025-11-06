package com.nucleo.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import com.nucleo.repository.MetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;


@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;


    public Meta criar(Meta meta) throws EntityNotCreatedException {
        try {
            if (meta.getStatus() == null) {
                meta.setStatus(StatusMeta.ativa);
            }
            return metaRepository.save(meta);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EntityNotCreatedException("Erro ao criar meta: " + e.getMessage());
        }
    }

    public List<Meta> listarPorUsuario(Long usuarioId) throws EntityNotFoundException {
        try {
            return metaRepository.findByUsuarioId(usuarioId);
        } catch (Exception e) {
            throw new EntityNotFoundException("meta.not-found");
        }
    }


    public Meta buscarPorId(Long id, Long usuarioId) {
        return metaRepository.findById(id)
                .filter(meta -> meta.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Meta não encontrada ou não pertence a este usuário."));
    }

    public Meta atualizar(Long id, Meta metaAtualizada, Long usuarioId) {

        try {

            Meta metaExistente = buscarPorId(id, usuarioId);

            atualizarSeDiferente(metaExistente::setTitulo, metaAtualizada.getTitulo(), metaExistente.getTitulo());
            atualizarSeDiferente(metaExistente::setValorAlvo, metaAtualizada.getValorAlvo(), metaExistente.getValorAlvo());
            atualizarSeDiferente(metaExistente::setDataLimite, metaAtualizada.getDataLimite(), metaExistente.getDataLimite());
            atualizarSeDiferente(metaExistente::setCategoriaId, metaAtualizada.getCategoriaId(), metaExistente.getCategoriaId());
            atualizarSeDiferente(metaExistente::setStatus, metaAtualizada.getStatus(), metaExistente.getStatus());

            return metaRepository.save(metaExistente);
        } catch (Exception e) {
            throw new EntityNotUpdatedException("meta.not-updated");
        }

    }

    public void cancelar(Long id, Long usuarioId) throws EntityNotDeletedException {
        try {

            Meta meta = buscarPorId(id, usuarioId);
            meta.setStatus(StatusMeta.cancelada);
            metaRepository.save(meta);
        } catch (Exception e) {
            throw new EntityNotDeletedException("meta.not-deleted");
        }
    }
}
