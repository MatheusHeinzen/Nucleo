package com.nucleo.service;

import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.exception.AuthenticationException;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.utils.EntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nucleo.security.SecurityUtils.getCurrentUserId;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> encontraTodos() {
        try {
            return usuarioRepository.findAll()
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    // ✅ para uso interno (service → service)
    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("usuario.not-found"));
    }

    // ✅ para uso externo (controller → response DTO)
    public UsuarioResponseDTO buscaUsuarioDTOPorId(Long id) {
        Usuario usuario = getUsuarioById(id);
        return toResponseDTO(usuario);
    }

    public Long getUsuarioIdLogado() {
        Long id = getCurrentUserId();
        if (id == null) {
            throw new AuthenticationException("error.auth");
        }
        return id;
    }

    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailAndAtivoTrue(email)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("usuario.not-found"));
    }

    public void deletaUsuario() {
        Long id = getCurrentUserId();
        try {
            if (id == null) throw new AuthenticationException("error.auth");
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new EntityNotDeletedException("usuario.not-deleted");
        }
    }

    public UsuarioResponseDTO atualizaUsuario(UsuarioRequestDTO dto) {
        try {
            Long id = getUsuarioIdLogado();
            Usuario usuario =  getUsuarioById(id);
            if(usuario == null) throw new AuthenticationException("error.auth");


            EntityUtils.atualizarSeDiferente(usuario::setEmail, dto.getEmail(), usuario.getEmail());
            EntityUtils.atualizarSeDiferente(usuario::setNome, dto.getNome(), usuario.getNome());
            EntityUtils.atualizarSeDiferente(usuario::setAtivo, dto.getAtivo(), usuario.getAtivo());

            EntityUtils.atualizarSeDiferente(usuario::setSenha, dto.getSenha(), usuario.getSenha());

            Usuario usuarioAtualizado = usuarioRepository.save(usuario);


            return toResponseDTO(usuarioAtualizado);

        }
        catch (AuthenticationException a){
            throw new AuthenticationException("error.auth");
        }catch (Exception e) {
            throw new EntityNotUpdatedException("usuario.not-updated");
        }
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        boolean isAdmin = usuario.getRoles() != null &&
                usuario.getRoles().contains(Usuario.Role.ROLE_ADMIN);

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .ativo(usuario.getAtivo())
                .isAdmin(isAdmin)
                .build();
    }

    public UsuarioResponseDTO buscarEuLogado(){
        Usuario usuario = getUsuarioById(getCurrentUserId());
        return toResponseDTO(usuario);
    }
}
