package com.nucleo.service;

import com.nucleo.dto.AlertaRequest;
import com.nucleo.exception.EntityNotCreatedException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Alerta;
import com.nucleo.model.Categoria;
import com.nucleo.model.ContasBancarias;
import com.nucleo.model.Usuario;
import com.nucleo.repository.AlertaRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.generic.BaseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.nucleo.utils.EntityUtils.atualizarSeDiferente;

@Service
public class AlertaService extends BaseService<Alerta, Long, AlertaRepository> {

    private final AlertaRepository alertaRepository;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final ContasBancariasService contasService;

    public AlertaService(AlertaRepository alertaRepository, UsuarioService usuarioService,
                        CategoriaService categoriaService, ContasBancariasService contasService) {
        super(alertaRepository);
        this.alertaRepository = alertaRepository;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.contasService = contasService;
    }

    public Alerta criar(AlertaRequest dto) throws EntityNotCreatedException {
        try {
            Usuario usuario = usuarioService.buscarEntidadePorId(SecurityUtils.getCurrentUserId());
            
            Categoria categoria = dto.getCategoriaId() != null 
                ? categoriaService.buscarPorId(dto.getCategoriaId()) 
                : null;
                
            ContasBancarias conta = dto.getContaId() != null 
                ? contasService.buscarPorId(dto.getContaId())
                : null;

            Alerta alerta = Alerta.builder()
                    .usuario(usuario)
                    .nomeRegra(dto.getNomeRegra())
                    .tipo(dto.getTipo())
                    .categoria(categoria)
                    .conta(conta)
                    .limiteValor(dto.getLimiteValor())
                    .janelaDias(dto.getJanelaDias())
                    .notificarEmail(dto.getNotificarEmail() != null ? dto.getNotificarEmail() : true)
                    .build();

            validarAlerta(alerta);
            return alertaRepository.save(alerta);
        } catch (Exception e) {
            throw new EntityNotCreatedException("alerta.not-created");
        }
    }

    public Alerta atualizar(Long id, AlertaRequest dto) throws EntityNotUpdatedException {
        try {
            Long usuarioId = SecurityUtils.getCurrentUserId();
            Alerta existente = buscarPorIdEUsuario(id, usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("alerta.not-found"));

            atualizarSeDiferente(existente::setNomeRegra, dto.getNomeRegra(), existente.getNomeRegra());
            atualizarSeDiferente(existente::setTipo, dto.getTipo(), existente.getTipo());
            
            if (dto.getCategoriaId() != null) {
                Categoria categoria = categoriaService.buscarPorId(dto.getCategoriaId());
                existente.setCategoria(categoria);
            }
            
            if (dto.getContaId() != null) {
                ContasBancarias conta = contasService.buscarPorId(dto.getContaId());
                existente.setConta(conta);
            }

            atualizarSeDiferente(existente::setLimiteValor, dto.getLimiteValor(), existente.getLimiteValor());
            atualizarSeDiferente(existente::setJanelaDias, dto.getJanelaDias(), existente.getJanelaDias());
            atualizarSeDiferente(existente::setNotificarEmail, dto.getNotificarEmail(), existente.getNotificarEmail());

            validarAlerta(existente);
            return alertaRepository.save(existente);
        } catch (EntityNotFoundException e) {
            throw new EntityNotUpdatedException("alerta.not-found");
        } catch (Exception e) {
            throw new EntityNotUpdatedException("alerta.not-updated");
        }
    }

    public void deletar(Long id) throws EntityNotDeletedException {
        try {
            Long usuarioId = SecurityUtils.getCurrentUserId();
            if (!existePorIdEUsuario(id, usuarioId)) {
                throw new EntityNotFoundException("alerta.not-found");
            }
            alertaRepository.softDelete(id);
        } catch (Exception e) {
            throw new EntityNotDeletedException("alerta.not-deleted");
        }
    }

    public List<Alerta> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return alertaRepository.findByUsuarioAndAtivoTrue(usuario);
    }

    public Page<Alerta> listarPorUsuario(Long usuarioId, Pageable pageable) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return alertaRepository.findByUsuarioAndAtivoTrue(usuario, pageable);
    }

    public Optional<Alerta> buscarPorIdEUsuario(Long id, Long usuarioId) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return alertaRepository.findByIdAndUsuarioAndAtivoTrue(id, usuario);
    }

    public List<Alerta> listarAtivosPorTipo(Long usuarioId, Alerta.TipoAlerta tipo) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return alertaRepository.findByUsuarioAndTipoAndAtivoTrue(usuario, tipo);
    }

    public boolean existePorIdEUsuario(Long id, Long usuarioId) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return alertaRepository.existsByIdAndUsuarioAndAtivoTrue(id, usuario);
    }

    private void validarAlerta(Alerta alerta) {
        if (alerta.getTipo() == null) {
            throw new IllegalArgumentException("Tipo de alerta é obrigatório");
        }

        switch (alerta.getTipo()) {
            case LIMITE_CATEGORIA:
                if (alerta.getCategoria() == null) {
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
                if (alerta.getConta() == null) {
                    throw new IllegalArgumentException("Conta é obrigatória para alertas do tipo SALDO_MINIMO");
                }
                if (alerta.getLimiteValor() == null) {
                    throw new IllegalArgumentException("Limite de valor é obrigatório para alertas do tipo SALDO_MINIMO");
                }
                break;
        }
    }
}
