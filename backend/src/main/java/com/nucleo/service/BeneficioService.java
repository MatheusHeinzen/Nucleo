package com.nucleo.service;

import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Beneficio;
import com.nucleo.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
public class BeneficioService {

    @Autowired
    private BeneficioRepository beneficioRepository;

    public Beneficio criar(Beneficio beneficio) throws EntityNotCreatedException {
        try {
            Beneficio novoBeneficio = beneficioRepository.save(beneficio);
            if (novoBeneficio == null) {
                throw new EntityNotCreatedException("beneficio.not-created");
            }
            return novoBeneficio;
        } catch (Exception e) {
            throw new EntityNotCreatedException("beneficio.not-created");
        }
    }

    public List<Beneficio> listarTodos() throws EntityNotFoundException {
        try {
            return beneficioRepository.findAllByAtivoTrue();
        } catch (Exception e) {
            throw new EntityNotFoundException("beneficio.not-found");
        }
    }

    public Beneficio buscarPorId(Long id) throws EntityNotFoundException {
        try {
            return beneficioRepository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException("Beneficio não encontrado"));
        } catch (Exception e) {
            throw new EntityNotFoundException("beneficio.not-found");
        }
    }

    public List<Beneficio> buscarPorUsuario(Long usuarioId) throws EntityNotFoundException {
        try {
            return beneficioRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
        } catch (Exception e) {
            throw new EntityNotFoundException("beneficio.not-found");
        }
    }

    public List<Beneficio> buscarPorTipo(Beneficio.TipoBeneficio tipo) throws EntityNotFoundException {
        try {
            return beneficioRepository.findByTipoAndAtivoTrue(tipo);
        } catch (Exception e) {
            throw new EntityNotFoundException("beneficio.not-found");
        }
    }

    public List<Beneficio> buscarPorUsuarioETipo(Long usuarioId, Beneficio.TipoBeneficio tipo) throws EntityNotFoundException {
        try {
            return beneficioRepository.findByUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
        } catch (Exception e) {
            throw new EntityNotFoundException("beneficio.not-found");
        }
    }

    public Beneficio atualizar(Long id, Beneficio beneficioAtualizado) throws EntityNotUpdatedException {
        try {
            Beneficio existente = buscarPorId(id);

            atualizarSeDiferente(existente::setNome, beneficioAtualizado.getNome(), existente.getNome());
            atualizarSeDiferente(existente::setDescricao, beneficioAtualizado.getDescricao(), existente.getDescricao());
            atualizarSeDiferente(existente::setTipo, beneficioAtualizado.getTipo(), existente.getTipo());
            atualizarSeDiferente(existente::setValor, beneficioAtualizado.getValor(), existente.getValor());

            return beneficioRepository.save(existente);
        } catch (EntityNotFoundException e) {
            throw new EntityNotUpdatedException("beneficio.not-found");
        } catch (Exception e) {
            throw new EntityNotUpdatedException("beneficio.not-updated");
        }
    }

    public void deletar(Long id) throws EntityNotDeletedException {
        try {
            beneficioRepository.softDelete(id);
        } catch (Exception e) {
            throw new EntityNotDeletedException("beneficio.not-deleted");
        }
    }
}

