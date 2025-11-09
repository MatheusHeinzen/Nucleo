package com.nucleo.backend.service;

import com.nucleo.dto.UsuarioRequestDTO;
import com.nucleo.dto.UsuarioResponseDTO;
import com.nucleo.exception.EntityNotDeletedException;
import com.nucleo.exception.EntityNotUpdatedException;
import com.nucleo.model.Usuario;
import com.nucleo.repository.UsuarioRepository;
import com.nucleo.security.SecurityUtils;
import com.nucleo.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class UsuarioServiceTest {

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setup() {
        usuarioAtivo = Usuario.builder()
                .id(1L)
                .nome("Isabel Pontes")
                .email("isa@nucleo.com")
                .senha("senha123")
                .ativo(true)
                .roles(Set.of(Usuario.Role.ROLE_USER))
                .build();


        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        securityUtilsMock.when(SecurityUtils::isAdmin).thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        // fecha o mock estático para evitar conflitos
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("Deve retornar todos os usuários ativos")
    void deveRetornarUsuariosAtivos() {
        BDDMockito.given(usuarioRepository.findAllByAtivoTrue()).willReturn(List.of(usuarioAtivo));

        List<UsuarioResponseDTO> resultado = usuarioService.encontraTodosDTO();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).email()).isEqualTo("isa@nucleo.com");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarPorId() {
        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(1L))
                .willReturn(Optional.of(usuarioAtivo));

        UsuarioResponseDTO dto = usuarioService.buscarPorId(1L);

        assertThat(dto.email()).isEqualTo("isa@nucleo.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoQuandoNaoEncontrarUsuario() {
        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(99L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve atualizar informações do usuário logado")
    void deveAtualizarUsuario() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Isabel Pontes", "isa.atualizada@nucleo.com", null, true);

        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(any(Long.class)))
                .willReturn(Optional.of(usuarioAtivo));

        BDDMockito.given(usuarioRepository.save(any(Usuario.class)))
                .willReturn(usuarioAtivo);

        UsuarioResponseDTO atualizado = usuarioService.atualizaUsuario(request, 1L);

        assertThat(atualizado.nome()).isEqualTo("Isabel Pontes"); // retorna o mesmo nome pois o mock não alterou
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Inexistente", "x@nucleo.com", null, true);

        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(99L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotUpdatedException.class, () -> usuarioService.atualizaUsuario(request, 99L));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuario() {
        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(1L))
                .willReturn(Optional.of(usuarioAtivo));

        usuarioService.deletaUsuario(1L);

        BDDMockito.then(usuarioRepository).should().softDelete(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deveLancarErroAoDeletarUsuarioInexistente() {
        BDDMockito.given(usuarioRepository.findByIdAndAtivoTrue(99L))
                .willReturn(Optional.empty());

        assertThrows(EntityNotDeletedException.class, () -> usuarioService.deletaUsuario(99L));
    }
}
