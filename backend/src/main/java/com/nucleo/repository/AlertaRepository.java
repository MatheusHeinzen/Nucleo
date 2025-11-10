package com.nucleo.repository;

import com.nucleo.model.Alerta;
import com.nucleo.model.Usuario;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaRepository extends BaseRepository<Alerta, Long> {

    List<Alerta> findByUsuarioAndAtivoTrue(Usuario usuario);

    Page<Alerta> findByUsuarioAndAtivoTrue(Usuario usuario, Pageable pageable);

    Optional<Alerta> findByIdAndUsuarioAndAtivoTrue(Long id, Usuario usuario);

    List<Alerta> findByUsuarioAndTipoAndAtivoTrue(Usuario usuario, Alerta.TipoAlerta tipo);

    boolean existsByIdAndUsuarioAndAtivoTrue(Long id, Usuario usuario);
}
