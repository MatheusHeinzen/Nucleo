package com.nucleo.service;

import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.utils.EntityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import static com.nucleo.security.SecurityUtils.getCurrentUserEmail;


@Service
public class UsuarioService  {
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtém o ID do usuário logado
     */

    public List<Usuario> encontraTodos() throws EntityNotFoundException {
        try {
            return usuarioRepository.findAll();
        }
        catch (Exception e){
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public Long getUsuarioIdLogado() throws EntityNotFoundException {
        try{
            String email = SecurityUtils.getCurrentUserEmail();
            if (email == null) {
                throw new RuntimeException("Usuário não autenticado");
            }

            Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            return usuario.getId();
        } catch (Exception e) {
            throw new EntityNotFoundException("usuario.not-found");
        }

    }

    public Usuario buscarUsuarioPorEmail(String email) {
        try{
            Optional<Usuario> usuario = usuarioRepository.findByEmailAndAtivoTrue(getCurrentUserEmail());
            if(usuario.isPresent()){
                return usuario.get();
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }


    public void deletaUsuario() throws EntityNotDeletedException {
        String email = getCurrentUserEmail();
        try{
            Optional<Usuario> usuario = usuarioRepository.findByEmailAndAtivoTrue(email);
            usuario.ifPresent(usuarioRepository::delete);
        }catch(Exception e){
            throw new EntityNotDeletedException("usuario.not-deleted");
        }

    }

    public Usuario atualizaUsuario( @Valid @RequestBody Usuario usuarioDetails) {

        try{
            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmailAndAtivoTrue(usuarioDetails.getEmail());

            if (usuarioOptional.isEmpty()) {
                return null;
            }

            Usuario usuario = usuarioOptional.get();

            EntityUtils.atualizarSeDiferente(usuario::setEmail,usuarioDetails.getEmail(),usuario.getEmail());
            EntityUtils.atualizarSeDiferente(usuario::setNome,usuarioDetails.getNome(),usuario.getNome());

            EntityUtils.atualizarSeDiferente(usuario::setAtivo,usuarioDetails.getAtivo(),usuario.getAtivo());

            return usuarioRepository.save(usuario);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
