package com.nucleo.service;

import com.nucleo.dto.CategoriaRequestDTO;
import com.nucleo.dto.CategoriaResponseDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        try {
            Categoria categoria = Categoria.builder()
                    .nome(dto.getNome())
                    .descricao(dto.getDescricao())
                    .tipo(dto.getTipo())
                    .build();

            Categoria salva = categoriaRepository.save(categoria);
            return CategoriaResponseDTO.fromEntity(salva);
        } catch (Exception e) {
            throw new EntityNotCreatedException("categoria.not-created");
        }
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAllByAtivoTrue().stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("categoria.not-found"));
    }

    public CategoriaResponseDTO buscarPorIdDTO(Long id) {
        return CategoriaResponseDTO.fromEntity(buscarPorId(id));
    }

    public List<CategoriaResponseDTO> buscarPorTipo(Categoria.TipoCategoria tipo) {
        return categoriaRepository.findByTipoAndAtivoTrue(tipo).stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        try {
            Categoria existente = buscarPorId(id);

            atualizarSeDiferente(existente::setNome, dto.getNome(), existente.getNome());
            atualizarSeDiferente(existente::setDescricao, dto.getDescricao(), existente.getDescricao());
            atualizarSeDiferente(existente::setTipo, dto.getTipo(), existente.getTipo());

            Categoria atualizada = categoriaRepository.save(existente);
            return CategoriaResponseDTO.fromEntity(atualizada);
        } catch (EntityNotFoundException e) {
            throw new EntityNotUpdatedException("categoria.not-found");
        } catch (Exception e) {
            throw new EntityNotUpdatedException("categoria.not-updated");
        }
    }

    public void deletar(Long id) {
        try {
            categoriaRepository.softDelete(id);
        } catch (Exception e) {
            throw new EntityNotDeletedException("categoria.not-deleted");
        }
    }
}
