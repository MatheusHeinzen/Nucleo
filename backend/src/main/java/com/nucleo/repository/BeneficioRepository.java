package com.nucleo.repository;

import com.nucleo.model.Beneficio;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficioRepository extends BaseRepository<Beneficio, Long> {

    List<Beneficio> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<Beneficio> findByTipoAndAtivoTrue(Beneficio.TipoBeneficio tipo);

    List<Beneficio> findByUsuarioIdAndTipoAndAtivoTrue(Long usuarioId, Beneficio.TipoBeneficio tipo);
}

