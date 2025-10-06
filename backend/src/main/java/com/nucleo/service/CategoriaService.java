package com.nucleo.service;

import com.nucleo.model.Categoria;
import com.nucleo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria criar(Categoria categoria) {
        // TODO: Atribuir o ID do usuário LOGADO antes de salvar
        // Ex: categoria.setUsuarioId(idDoUsuarioLogado);
        return categoriaRepository.save(categoria);
    }

    // Lógica para o endpoint GET (todos)
    public List<Categoria> listarPorUsuario(Long usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    // Lógica para o endpoint GET (por ID)
    public Categoria buscarPorId(Long id, Long usuarioId) {
        // Busca a categoria e garante que ela pertence ao usuário logado
        return categoriaRepository.findById(id)
                .filter(categoria -> categoria.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para este usuário."));
    }

    // Lógica para o endpoint PUT
    public Categoria atualizar(Long id, Categoria categoriaAtualizada, Long usuarioId) {
        Categoria categoriaExistente = buscarPorId(id, usuarioId); // Reutiliza a busca segura

        // Atualiza os campos
        categoriaExistente.setNome(categoriaAtualizada.getNome());
        categoriaExistente.setTipo(categoriaAtualizada.getTipo());
        categoriaExistente.setCorHex(categoriaAtualizada.getCorHex());

        return categoriaRepository.save(categoriaExistente);
    }

    // Lógica para o endpoint DELETE (Soft Delete)
    public void deletar(Long id, Long usuarioId) {
        Categoria categoria = buscarPorId(id, usuarioId); // Reutiliza a busca segura
        categoria.setAtiva(false); // Apenas marca como inativa
        categoriaRepository.save(categoria);
    }
}
