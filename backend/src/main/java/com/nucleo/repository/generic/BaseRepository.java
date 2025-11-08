package com.nucleo.repository.generic;

import com.nucleo.model.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    Optional<T> findByIdAndAtivoTrue(ID id);

    List<T> findAllByAtivoTrue();

    Page<T> findAllByAtivoTrue(Pageable pageable);

    @Query("UPDATE #{#entityName} e SET e.ativo = false WHERE e.id = :id")
    @Modifying
    @org.springframework.transaction.annotation.Transactional
    void softDelete(@Param("id") ID id);

    boolean existsByIdAndAtivoTrue(ID id);

}