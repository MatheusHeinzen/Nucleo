package com.nucleo.repository;

import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.model.Usuario;
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

    Transacao findTransacaoByUsuario_Id(Long id);
    Transacao findTransacaoByUsuario_IdAndId(Long usuarioId, Long id);

    List<Transacao> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);

    Page<Transacao> findAllByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable pageable);

    List<Transacao> findAllByUsuarioIdAndDataBetweenAndAtivoTrue(
            Long usuarioId, LocalDate inicio, LocalDate fim);


    List<Transacao> findAllByUsuarioIdAndCategoriaIdAndAtivoTrue(Long usuarioId, Long categoriaId);

    List<Transacao> findAllByUsuarioIdAndTipoAndAtivoTrue(
            Long usuarioId, Transacao.TipoTransacao tipo);

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo AND t.ativo = true")
    BigDecimal sumValorByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") Transacao.TipoTransacao tipo);

    @Query("SELECT t FROM Transacao t WHERE t.usuario.id = :usuarioId AND t.data BETWEEN :inicio AND :fim")
    List<Transacao> findByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}