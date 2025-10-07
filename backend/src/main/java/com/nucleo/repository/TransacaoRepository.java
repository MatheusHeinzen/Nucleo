package com.nucleo.repository;

import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.repository.generic.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends BaseRepository<Transacao, Long> {

    // Buscar transações por usuário
    List<Transacao> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);

    Page<Transacao> findAllByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);

    // Buscar por período
    List<Transacao> findAllByUsuarioIdAndDataBetweenAndAtivoTrue(
            Long usuarioId, LocalDate inicio, LocalDate fim);

    // Buscar por categoria
    List<Transacao> findAllByUsuarioIdAndCategoriaAndAtivoTrue(
            Long usuarioId, Categoria categoria);

    // Buscar por tipo (entrada/saída)
    List<Transacao> findAllByUsuarioIdAndTipoAndAtivoTrue(
            Long usuarioId, Transacao.TipoTransacao tipo);

    // Soma de valores por tipo
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo AND t.ativo = true")
    BigDecimal sumValorByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") Transacao.TipoTransacao tipo);
}