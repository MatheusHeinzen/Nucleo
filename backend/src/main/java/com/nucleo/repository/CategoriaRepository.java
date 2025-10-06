package com.nucleo.repository;

import com.nucleo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Spring Data JPA criará a query "SELECT * FROM categorias WHERE usuario_id = ?"
    // Isso será essencial para a segurança da sua aplicação.
    List<Categoria> findByUsuarioId(Long usuarioId);

    // Você pode adicionar mais queries customizadas aqui se precisar
}

