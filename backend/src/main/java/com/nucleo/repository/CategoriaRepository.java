package com.nucleo.repository;

import com.nucleo.model.Categoria;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria, Long> {

    List<Categoria> findByTipoAndAtivoTrue(Categoria.TipoCategoria tipo);

    Optional<Categoria> findByNome(String nome);

}