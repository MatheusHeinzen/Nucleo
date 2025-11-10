package com.nucleo.service;

import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.utils.EntityUtils;
import com.nucleo.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import static com.nucleo.security.SecurityUtils.getCurrentUserId;


@Service
@RequiredArgsConstructor
public class UsuarioService  {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> encontraTodosDTO() throws EntityNotFoundException {
        try {
            return UsuarioResponseDTO.fromEntity(usuarioRepository.findAllByAtivoTrue());
        }
        catch (Exception e){
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public UsuarioResponseDTO buscarUsuarioDTOPorEmail(String email) throws EntityNotFoundException {
        try{
            return UsuarioResponseDTO.fromEntity(usuarioRepository.findByEmailAndAtivoTrue(email)
                    .orElseThrow(() -> new EntityNotFoundException("usuario.not-found")));
        }catch (Exception e){
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public UsuarioResponseDTO buscarPorId(Long id) throws EntityNotFoundException {
        try {
            return UsuarioResponseDTO.fromEntity(usuarioRepository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException("usuario.not-found")));
        } catch (Exception e) {
            throw new EntityNotFoundException("usuario.not-found");
        }
    }

    public Usuario buscarEntidadePorId(Long id) throws EntityNotFoundException {
        try {
            return usuarioRepository.findByIdAndAtivoTrue(id)
                    .orElseThrow(() -> new EntityNotFoundException("usuario.not-found"));
        } catch (Exception e) {
            throw new EntityNotFoundException("usuario.not-found");
        }
    }




    public void deletaUsuario() throws EntityNotDeletedException {

        try{
            usuarioRepository.softDelete(getCurrentUserId());
        }catch(Exception e){
            throw new EntityNotDeletedException("usuario.not-found");
        }
    }

    public void deletaUsuario(Long usuarioId) throws EntityNotDeletedException {
        try{
            Optional<Usuario> usuario = usuarioRepository.findByIdAndAtivoTrue(usuarioId);
            if (usuario.isPresent()) {
                usuarioRepository.softDelete(usuarioId);
            }else{
                throw new EntityNotFoundException("usuario.not-found");
            }
        }catch(Exception e){
            throw new EntityNotDeletedException("usuario.not-deleted");
        }
    }

    public UsuarioResponseDTO atualizaUsuario(UsuarioRequestDTO usuarioDetails) throws EntityNotUpdatedException {

        try{
            System.out.println(usuarioDetails);

            System.out.println("usuariodetailssss");
            System.out.println(usuarioDetails.ativo());
            Optional<Usuario> usuarioOptional = usuarioRepository.findByIdAndAtivoTrue(getCurrentUserId());
            System.out.println(usuarioOptional.isPresent());
            if (usuarioOptional.isEmpty()) {
                throw new EntityNotUpdatedException("usuario.not-found");
            }

            Usuario usuario = usuarioOptional.get();

            EntityUtils.atualizarSeDiferente(usuario::setEmail,usuarioDetails.email(),usuario.getEmail());
            EntityUtils.atualizarSeDiferente(usuario::setNome,usuarioDetails.nome(),usuario.getNome());

            if (usuarioDetails.ativo()!=null) {
                EntityUtils.atualizarSeDiferente(usuario::setAtivo, usuarioDetails.ativo(), usuario.getAtivo());
            }
            if (usuarioDetails.senha() != null && !usuarioDetails.senha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(usuarioDetails.senha()));
            }
            return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));

        }catch (Exception e){
            e.printStackTrace();
            throw new  EntityNotUpdatedException("usuario.not-updated");
        }

    }


    public UsuarioResponseDTO atualizaUsuario(@RequestBody UsuarioRequestDTO usuarioDetails, Long id) throws EntityNotUpdatedException {

        try{
            Optional<Usuario> usuarioOptional = usuarioRepository.findByIdAndAtivoTrue(id);

            if (usuarioOptional.isEmpty()) {
                throw new EntityNotUpdatedException("usuario.not-found");
            }

            Usuario usuario = usuarioOptional.get();

            EntityUtils.atualizarSeDiferente(usuario::setEmail, usuarioDetails.email(), usuario.getEmail());
            EntityUtils.atualizarSeDiferente(usuario::setNome,usuarioDetails.nome(),usuario.getNome());
            EntityUtils.atualizarSeDiferente(usuario::setAtivo,usuarioDetails.ativo(),usuario.getAtivo());

            return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));

        }catch (Exception e){
            e.printStackTrace();
            throw new  EntityNotUpdatedException("usuario.not-updated");
        }

    }



}
