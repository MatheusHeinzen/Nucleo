package com.nucleo.service;

import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.generic.BaseService;
import com.nucleo.utils.EntityUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static com.nucleo.security.SecurityUtils.getCurrentUserEmail;


@Service

public class UsuarioService extends BaseService<Usuario,Long, UsuarioRepository> {
    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository repository, UsuarioRepository usuarioRepository) {
        super(repository);
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtém o ID do usuário logado
     */
    public Long getUsuarioIdLogado() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuario.getId();
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


    public boolean deletaUsuario(String email) {
        try{
        Optional<Usuario> usuario = usuarioRepository.findByEmailAndAtivoTrue(email);
            usuario.ifPresent(usuarioRepository::delete);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public Usuario atualizaUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioDetails) {

        try{
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

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
