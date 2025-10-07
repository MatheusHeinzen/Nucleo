package com.nucleo.config;

import com.nucleo.model.Categoria;
import com.nucleo.model.Transacao;
import com.nucleo.model.Usuario;
import com.nucleo.repository.CategoriaRepository;
import com.nucleo.repository.TransacaoRepository;
import com.nucleo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdmin();
        criarCategoriasPadrao();
        criarTransacoesExemplo();
    }

    private void criarUsuarioAdmin() {
        if (usuarioRepository.findByEmailAndAtivoTrue("admin@nucleo.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@nucleo.com")
                    .senha(passwordEncoder.encode("123456"))
                    .roles(Set.of(Usuario.Role.ROLE_ADMIN))
                    .build();

            usuarioRepository.save(admin);
            System.out.println("✅ Usuário admin criado: admin@nucleo.com / 123456");
        }
    }

    private void criarCategoriasPadrao() {
        if (categoriaRepository.count() == 0) {
            categoriaRepository.save(Categoria.builder()
                    .nome("ALIMENTACAO")
                    .descricao("Gastos com alimentação")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("TRANSPORTE")
                    .descricao("Gastos com transporte")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("MORADIA")
                    .descricao("Gastos com moradia")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("LAZER")
                    .descricao("Gastos com lazer")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("SAUDE")
                    .descricao("Gastos com saúde")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("EDUCACAO")
                    .descricao("Gastos com educação")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("SALARIO")
                    .descricao("Receita de salário")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("INVESTIMENTOS")
                    .descricao("Receita de investimentos")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("OUTROS")
                    .descricao("Outras receitas")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            System.out.println("✅ Categorias padrão criadas!");
        }
    }

    private void criarTransacoesExemplo() {
        if (transacaoRepository.count() == 0) {
            var admin = usuarioRepository.findByEmailAndAtivoTrue("admin@nucleo.com").orElseThrow();
            var categoriaSalario = categoriaRepository.findByNome("SALARIO");
            var categoriaAlimentacao = categoriaRepository.findByNome("ALIMENTACAO");
            var categoriaLazer = categoriaRepository.findByNome("LAZER");

            if (categoriaSalario.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Salário Setembro")
                        .valor(new BigDecimal("3000.00"))
                        .data(LocalDate.now().minusDays(5))
                        .tipo(Transacao.TipoTransacao.ENTRADA)
                        .categoria(categoriaSalario.get())
                        .usuario(admin)
                        .build());
            }

            if (categoriaAlimentacao.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Supermercado")
                        .valor(new BigDecimal("250.50"))
                        .data(LocalDate.now().minusDays(2))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaAlimentacao.get())
                        .usuario(admin)
                        .build());
            }

            if (categoriaLazer.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Cinema")
                        .valor(new BigDecimal("45.00"))
                        .data(LocalDate.now().minusDays(1))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaLazer.get())
                        .usuario(admin)
                        .build());
            }

            System.out.println("✅ Transações exemplo criadas!");
        }
    }
}