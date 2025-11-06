package com.nucleo.repository;

import com.matheus.Nucleo.model.ContasBancarias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContasBancariasRepository extends JpaRepository<ContasBancarias, Long> {

    // Busca todas as contas ATIVAS de um usuário
    List<ContasBancarias> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    // Busca uma conta ATIVA específica de um usuário pelo ID
    Optional<ContasBancarias> findByIdAndUsuarioIdAndAtivoTrue(Long id, Long usuarioId);

    // Busca uma conta de um usuário (ativa ou não)
    // Útil para o método de deleção, para garantir que a conta existe antes de inativar
    Optional<ContasBancarias> findByIdAndUsuarioId(Long id, Long usuarioId);
}