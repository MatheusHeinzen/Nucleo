//package com.nucleo.service.generic;
//
//import com.nucleo.model.base.BaseEntity;
//import com.nucleo.repository.generic.BaseRepository;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//@AllArgsConstructor
//@RequiredArgsConstructor
//public abstract class BaseService<T extends BaseEntity, ID, R extends BaseRepository<T, ID>> {
//
//    protected final R repository;
//
//    public Optional<T> findById(ID id) {
//        return repository.findByIdAndAtivoTrue(id);
//    }
//
//    public List<T> findAll() {
//        return repository.findAllByAtivoTrue();
//    }
//
//    public Page<T> findAll(Pageable pageable) {
//        return repository.findAllByAtivoTrue(pageable);
//    }
//
//    public T save(T entity) {
//        return repository.save(entity);
//    }
//
//    public void delete(ID id) {
//        repository.softDelete(id);
//    }
//
//    public boolean exists(ID id) {
//        return repository.existsByIdAndAtivoTrue(id);
//    }
//}