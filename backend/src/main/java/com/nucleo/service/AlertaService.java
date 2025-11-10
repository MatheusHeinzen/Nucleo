package com.nucleo.service;

import com.nucleo.model.Alerta;
import com.nucleo.repository.AlertaRepository;
import com.nucleo.service.generic.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertaService extends BaseService<Alerta, Long, AlertaRepository> {

    public AlertaService(AlertaRepository repository) {
        super(repository);
    }

    public List<Alerta> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Page<Alerta> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioIdAndAtivoTrue(usuarioId, pageable);
    }

    public Optional<Alerta> buscarPorIdEUsuario(Long id, Long usuarioId) {
        return repository.findByIdAndUsuarioIdAndAtivoTrue(id, usuarioId);
    }

    public List<Alerta> listarAtivosPorTipo(Long usuarioId, Alerta.TipoAlerta tipo) {
        return repository.findByUsuarioIdAndTipoAndAtivoTrue(usuarioId, tipo);
    }

    public boolean existePorIdEUsuario(Long id, Long usuarioId) {
        return repository.existsByIdAndUsuarioIdAndAtivoTrue(id, usuarioId);
    }

    /**
     * Valida regras de negócio específicas de acordo com o tipo de alerta.
     * Lança IllegalArgumentException em caso de inconsistência.
     */
    public void validarAlerta(Alerta alerta) {
        if (alerta.getTipo() == null) {
            throw new IllegalArgumentException("Tipo de alerta é obrigatório");
        }

        switch (alerta.getTipo()) {
            case LIMITE_CATEGORIA:
                if (alerta.getCategoriaId() == null) {
                    throw new IllegalArgumentException("Categoria é obrigatória para alertas do tipo LIMITE_CATEGORIA");
                }
                if (alerta.getLimiteValor() == null) {
                    throw new IllegalArgumentException("Limite de valor é obrigatório para alertas do tipo LIMITE_CATEGORIA");
                }
                break;

            case GASTO_ATIPICO:
                if (alerta.getJanelaDias() == null) {
                    throw new IllegalArgumentException("Janela de dias é obrigatória para alertas do tipo GASTO_ATIPICO");
                }
                break;

            case SALDO_MINIMO:
                if (alerta.getContaId() == null) {
                    throw new IllegalArgumentException("Conta é obrigatória para alertas do tipo SALDO_MINIMO");
                }
                if (alerta.getLimiteValor() == null) {
                    throw new IllegalArgumentException("Limite de valor é obrigatório para alertas do tipo SALDO_MINIMO");
                }
                break;
        }
    }
}
