package com.nucleo.repository;

import com.nucleo.model.Alerta;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaRepository extends BaseRepository<Alerta, Long> {

    List<Alerta> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    Page<Alerta> findByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);

    Optional<Alerta> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);

    List<Alerta> findByUsuarioIdAndTipoAndAtivoTrue(Long usuarioId, Alerta.TipoAlerta tipo);

    boolean existsByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);
}
