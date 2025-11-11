package com.nucleo.service;

import com.nucleo.dto.CategoriaRequestDTO;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotFoundException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.model.Usuario;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioService usuarioService;

    public Categoria criar(CategoriaRequestDTO dto) throws EntityNotCreatedException {
        try {
            Usuario usuario = usuarioService.buscarEntidadePorId(SecurityUtils.getCurrentUserId());

            Categoria categoria = Categoria.builder()
                    .nome(dto.nome())
                    .descricao(dto.descricao())
                    .tipo(dto.tipo())
                    .usuario(usuario)
                    .isGlobal(false)
                    .build();

            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new EntityNotCreatedException("categoria.not-created");
        }
    }

    public List<Categoria> listarPorUsuario() {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        return categoriaRepository.findByIsGlobalTrueOrUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Categoria buscarPorId(Long id) throws EntityNotFoundException {
        return categoriaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("categoria.not-found"));
    }

    public List<Categoria> buscarPorTipo(Categoria.TipoCategoria tipo) {
        Long usuarioId = SecurityUtils.getCurrentUserId();
        return categoriaRepository.findByIsGlobalTrueOrUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
    }

    public Categoria atualizar(Long id, CategoriaRequestDTO dto) throws EntityNotUpdatedException {
        try {
            Categoria existente = buscarPorId(id);

            atualizarSeDiferente(existente::setNome, dto.nome(), existente.getNome());
            atualizarSeDiferente(existente::setDescricao, dto.descricao(), existente.getDescricao());
            atualizarSeDiferente(existente::setTipo, dto.tipo(), existente.getTipo());

            return categoriaRepository.save(existente);
        } catch (EntityNotFoundException e) {
            throw new EntityNotUpdatedException("categoria.not-found");
        } catch (Exception e) {
            throw new EntityNotUpdatedException("categoria.not-updated");
        }
    }

    public void deletar(Long id) throws EntityNotDeletedException {
        try {
            categoriaRepository.softDelete(id);
        } catch (Exception e) {
            throw new EntityNotDeletedException("categoria.not-deleted");
        }
    }
}
