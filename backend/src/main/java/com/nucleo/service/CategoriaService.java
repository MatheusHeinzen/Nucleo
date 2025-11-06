package com.nucleo.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria criar(Categoria categoria) throws EntityNotCreatedException {
        try{
            return categoriaRepository.save(categoria);
        }catch (Exception e) {
            throw new EntityNotCreatedException("categoria.not-created");
        }
    }

    public List<Categoria> listarTodas() throws EntityNotFoundException{
        try{
        return categoriaRepository.findAllByAtivoTrue();
        }catch (Exception e) {
            throw new EntityNotFoundException("categoria.not-found");
        }
    }

    public Categoria buscarPorId(Long id) throws EntityNotFoundException{
        try{
            return categoriaRepository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        }
        catch (Exception e) {
            throw new EntityNotFoundException("categoria.not-found");
        }
    }

    public List<Categoria> buscarPorTipo(Categoria.TipoCategoria tipo) throws EntityNotFoundException {
        try{
            return categoriaRepository.findByTipoAndAtivoTrue(tipo);
        }catch (Exception e){
            throw new EntityNotFoundException("categoria.not-found");
        }
    }

    public Categoria atualizar(Long id, Categoria categoriaAtualizada) throws EntityNotUpdatedException {
        try{

        Categoria existente = buscarPorId(id);

        atualizarSeDiferente(existente::setNome, categoriaAtualizada.getNome(), existente.getNome());
        atualizarSeDiferente(existente::setDescricao, categoriaAtualizada.getDescricao(), existente.getDescricao());
        atualizarSeDiferente(existente::setTipo, categoriaAtualizada.getTipo(), existente.getTipo());

        return categoriaRepository.save(existente);
        }catch (EntityNotFoundException e) {
            throw new EntityNotUpdatedException("categoria.not-found");
        }catch (Exception e) {
            throw new EntityNotUpdatedException("categoria.not-updated");
        }
    }

    public void deletar(Long id) throws EntityNotDeletedException {
        try{
            categoriaRepository.softDelete(id);
        }catch (Exception e) {
            throw new EntityNotDeletedException("categoria.not-deleted");
        }
    }
}
