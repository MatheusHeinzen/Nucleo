package com.nucleo.service;

import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.utils.EntityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import static com.nucleo.security.SecurityUtils.getCurrentUserId;


@Service
@RequiredArgsConstructor
public class UsuarioService  {
    private final UsuarioRepository usuarioRepository;

    public List<Usuario> encontraTodos() throws EntityNotFoundException {
        try {
            return usuarioRepository.findAllByAtivoTrue();
        }
        catch (Exception e){
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public Usuario buscarUsuarioPorEmail(String email) throws EntityNotFoundException {
        try{
            return usuarioRepository.findByEmailAndAtivoTrue(email)
                    .orElseThrow(() -> new EntityNotFoundException("usuario.not-found"));
        }catch (Exception e){
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public Usuario buscarPorId(Long id) throws EntityNotFoundException {
        try {
            return usuarioRepository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException("usuario.not-found"));
        } catch (Exception e) {
            throw new EntityNotFoundException("usuario.not-found");
        }
    }


    public void deletaUsuario() throws EntityNotDeletedException {
        Long usuarioId = getCurrentUserId();
        try{
            Optional<Usuario> usuario = usuarioRepository.findByIdAndAtivoTrue(usuarioId);
            if (usuario.isPresent()) {
                usuarioRepository.softDelete(usuarioId);
            }
        }catch(Exception e){
            throw new EntityNotDeletedException("usuario.not-deleted");
        }
    }

    public Usuario atualizaUsuario( @Valid @RequestBody Usuario usuarioDetails) throws EntityNotUpdatedException {

        try{
            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailAndAtivoTrue(usuarioDetails.getEmail());

            if (usuarioOptional.isEmpty()) {
                throw new EntityNotUpdatedException("usuario.not-found");
            }

            Usuario usuario = usuarioOptional.get();

            EntityUtils.atualizarSeDiferente(usuario::setEmail,usuarioDetails.getEmail(),usuario.getEmail());
            EntityUtils.atualizarSeDiferente(usuario::setNome,usuarioDetails.getNome(),usuario.getNome());

            EntityUtils.atualizarSeDiferente(usuario::setAtivo,usuarioDetails.getAtivo(),usuario.getAtivo());

            return usuarioRepository.save(usuario);

        }catch (Exception e){

            throw new  EntityNotUpdatedException("usuario.not-updated");
        }

    }

}
