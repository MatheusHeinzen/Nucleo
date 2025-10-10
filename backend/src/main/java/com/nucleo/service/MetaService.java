package com.nucleo.service;

import com.nucleo.dto.MetaRequestDTO;
import com.nucleo.dto.MetaResponseDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Meta;
import com.nucleo.model.StatusMeta;
import com.nucleo.repository.MetaRepository;
import com.nucleo.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
public class MetaService {

    @Autowired
    private MetaRepository metaRepository;

    public MetaResponseDTO criar(MetaRequestDTO dto) {
        try {
            Meta meta = new Meta();
            meta.setUsuarioId(SecurityUtils.getCurrentUserId());
            meta.setTitulo(dto.getTitulo());
            meta.setValorAlvo(dto.getValorAlvo());
            meta.setDataLimite(dto.getDataLimite());
            meta.setCategoriaId(dto.getCategoriaId());
            meta.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusMeta.ativa);

            Meta salva = metaRepository.save(meta);
            return MetaResponseDTO.fromEntity(salva);
        } catch (Exception e) {
            throw new EntityNotCreatedException("meta.not-created");
        }
    }

    public List<MetaResponseDTO> listarPorUsuario(Long usuarioId) {
        try {
            return metaRepository.findByUsuarioId(usuarioId).stream()
                    .map(MetaResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EntityNotFoundException("meta.not-found");
        }
    }

    public Meta buscarPorId(Long id, Long usuarioId) {
        return metaRepository.findById(id)
                .filter(meta -> meta.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("meta.not-found"));
    }

    public MetaResponseDTO atualizar(Long id, MetaRequestDTO dto, Long usuarioId) {
        try {
            Meta existente = buscarPorId(id, usuarioId);

            atualizarSeDiferente(existente::setTitulo, dto.getTitulo(), existente.getTitulo());
            atualizarSeDiferente(existente::setValorAlvo, dto.getValorAlvo(), existente.getValorAlvo());
            atualizarSeDiferente(existente::setDataLimite, dto.getDataLimite(), existente.getDataLimite());
            atualizarSeDiferente(existente::setCategoriaId, dto.getCategoriaId(), existente.getCategoriaId());
            atualizarSeDiferente(existente::setStatus, dto.getStatus(), existente.getStatus());

            Meta atualizada = metaRepository.save(existente);
            return MetaResponseDTO.fromEntity(atualizada);
        } catch (Exception e) {
            throw new EntityNotUpdatedException("meta.not-updated");
        }
    }

    public void cancelar(Long id, Long usuarioId) {
        try {
            Meta meta = buscarPorId(id, usuarioId);
            meta.setStatus(StatusMeta.cancelada);
            metaRepository.save(meta);
        } catch (Exception e) {
            throw new EntityNotDeletedException("meta.not-deleted");
        }
    }
}
