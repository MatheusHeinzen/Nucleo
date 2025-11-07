package com.nucleo.repository;

import com.nucleo.model.ContasBancarias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContasBancariasRepository extends JpaRepository<ContasBancarias, Long> {

    List<ContasBancarias> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    Optional<ContasBancarias> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);

    Optional<ContasBancarias> findByIdAndUsuarioId(Long id, Long usuarioId);
}