package com.nucleo.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Categoria;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.utils.EntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria criar(Categoria categoria) throws EntityNotCreatedException {
        try{
            Categoria novaCategoria = categoriaRepository.save(categoria);
            if (novaCategoria == null) {
                throw new EntityNotCreatedException("categoria.not-created");
            }
            return novaCategoria;
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
        // supondo que é soft delete mesmo
    }
}
