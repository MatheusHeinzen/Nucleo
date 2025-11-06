package com.nucleo.repository;

import com.nucleo.model.Usuario;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
}