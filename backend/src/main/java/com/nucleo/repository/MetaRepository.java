package com.nucleo.repository;

import com.nucleo.model.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    // Método para buscar todas as metas de um usuário específico.
    // Essencial para a segurança e para a lógica do seu aplicativo.
    List<Meta> findByUsuarioId(Long usuarioId);
}