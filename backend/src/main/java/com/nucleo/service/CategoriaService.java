package com.nucleo.service;

import com.nucleo.model.Categoria;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.service.generic.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService extends BaseService<Categoria, Long, CategoriaRepository> {

    public CategoriaService(CategoriaRepository repository) {
        super(repository);
    }

    // ✅ O @RequiredArgsConstructor já injeta o repository automaticamente
    // O BaseService já recebe o repository via construtor da classe pai

    // Métodos específicos da Categoria
    public Categoria criar(Categoria categoria) {
        return save(categoria);
    }

    public List<Categoria> listarTodas() {
        return findAll();
    }

    public Categoria buscarPorId(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public List<Categoria> buscarPorTipo(Categoria.TipoCategoria tipo) {
        return repository.findByTipoAndAtivoTrue(tipo);
    }

    public Categoria atualizar(Long id, Categoria categoria) {
        Categoria categoriaExistente = buscarPorId(id);

        categoriaExistente.setNome(categoria.getNome());
        categoriaExistente.setDescricao(categoria.getDescricao());
        categoriaExistente.setTipo(categoria.getTipo());

        return save(categoriaExistente);
    }

    public void deletar(Long id) {
        delete(id); // Soft delete
    }
}