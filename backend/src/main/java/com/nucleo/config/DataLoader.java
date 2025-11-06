package com.nucleo.config;

import com.nucleo.model.*;
import com.nucleo.repository.*;
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
    private final BeneficioRepository beneficioRepository;
    private final MetaRepository metaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarios();
        criarCategoriasPadrao();
        criarBeneficiosExemplo();
        criarTransacoesExemplo();
        criarMetasExemplo();
    }

    private void criarUsuarios() {
        if (usuarioRepository.findByEmailAndAtivoTrue("admin@nucleo.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .email("admin@nucleo.com")
                    .senha(passwordEncoder.encode("123"))
                    .roles(Set.of(Usuario.Role.ROLE_ADMIN))
                    .build();
            usuarioRepository.save(admin);
            System.out.println("[OK] Usuário ADMIN criado: admin@nucleo.com / 123");
        }

        if (usuarioRepository.findByEmailAndAtivoTrue("joao@nucleo.com").isEmpty()) {
            Usuario joao = Usuario.builder()
                    .nome("João Silva")
                    .email("joao@nucleo.com")
                    .senha(passwordEncoder.encode("123"))
                    .roles(Set.of(Usuario.Role.ROLE_USER))
                    .build();
            usuarioRepository.save(joao);
            System.out.println("[OK] Usuário USER criado: joao@nucleo.com / 123");
        }
    }

    private void criarCategoriasPadrao() {
        if (categoriaRepository.count() == 0) {
            categoriaRepository.save(Categoria.builder()
                    .nome("Alimentação")
                    .descricao("Gastos com alimentação e refeições")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Transporte")
                    .descricao("Gastos com transporte")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Moradia")
                    .descricao("Gastos com moradia, aluguel e contas")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Lazer")
                    .descricao("Gastos com entretenimento e lazer")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Saúde")
                    .descricao("Gastos com saúde e medicamentos")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Educação")
                    .descricao("Gastos com educação e cursos")
                    .tipo(Categoria.TipoCategoria.SAIDA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Salário")
                    .descricao("Receita de salário mensal")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Investimentos")
                    .descricao("Receita de investimentos")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            categoriaRepository.save(Categoria.builder()
                    .nome("Freelance")
                    .descricao("Receita de trabalhos freelance")
                    .tipo(Categoria.TipoCategoria.ENTRADA)
                    .build());

            System.out.println("[OK] 9 Categorias padrão criadas!");
        }
    }

    private void criarBeneficiosExemplo() {
        if (beneficioRepository.count() == 0) {
            var joao = usuarioRepository.findByEmailAndAtivoTrue("joao@nucleo.com").orElseThrow();

            beneficioRepository.save(Beneficio.builder()
                    .nome("Vale Refeição")
                    .descricao("Benefício mensal de VR")
                    .tipo(Beneficio.TipoBeneficio.VR)
                    .valor(new BigDecimal("500.00"))
                    .usuario(joao)
                    .build());

            beneficioRepository.save(Beneficio.builder()
                    .nome("Vale Transporte")
                    .descricao("Benefício mensal de VT")
                    .tipo(Beneficio.TipoBeneficio.VT)
                    .valor(new BigDecimal("200.00"))
                    .usuario(joao)
                    .build());

            beneficioRepository.save(Beneficio.builder()
                    .nome("Plano de Saúde")
                    .descricao("Plano de saúde empresarial")
                    .tipo(Beneficio.TipoBeneficio.PLANO_SAUDE)
                    .valor(new BigDecimal("350.00"))
                    .usuario(joao)
                    .build());

            System.out.println("[OK] 3 Benefícios exemplo criados para João!");
        }
    }

    private void criarTransacoesExemplo() {
        if (transacaoRepository.count() == 0) {
            var joao = usuarioRepository.findByEmailAndAtivoTrue("joao@nucleo.com").orElseThrow();
            var categoriaSalario = categoriaRepository.findByNome("Salário");
            var categoriaAlimentacao = categoriaRepository.findByNome("Alimentação");
            var categoriaTransporte = categoriaRepository.findByNome("Transporte");
            var categoriaLazer = categoriaRepository.findByNome("Lazer");
            var categoriaFreelance = categoriaRepository.findByNome("Freelance");

            if (categoriaSalario.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Salário Novembro")
                        .valor(new BigDecimal("5000.00"))
                        .data(LocalDate.now().minusDays(5))
                        .tipo(Transacao.TipoTransacao.ENTRADA)
                        .categoria(categoriaSalario.get())
                        .usuario(joao)
                        .build());
            }

            if (categoriaFreelance.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Trabalho Freelance - Site")
                        .valor(new BigDecimal("1200.00"))
                        .data(LocalDate.now().minusDays(3))
                        .tipo(Transacao.TipoTransacao.ENTRADA)
                        .categoria(categoriaFreelance.get())
                        .usuario(joao)
                        .build());
            }

            if (categoriaAlimentacao.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Supermercado Atacadão")
                        .valor(new BigDecimal("350.50"))
                        .data(LocalDate.now().minusDays(4))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaAlimentacao.get())
                        .usuario(joao)
                        .build());

                transacaoRepository.save(Transacao.builder()
                        .descricao("Restaurante")
                        .valor(new BigDecimal("89.90"))
                        .data(LocalDate.now().minusDays(1))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaAlimentacao.get())
                        .usuario(joao)
                        .build());
            }

            if (categoriaTransporte.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Uber para trabalho")
                        .valor(new BigDecimal("45.00"))
                        .data(LocalDate.now().minusDays(2))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaTransporte.get())
                        .usuario(joao)
                        .build());
            }

            if (categoriaLazer.isPresent()) {
                transacaoRepository.save(Transacao.builder()
                        .descricao("Cinema IMAX")
                        .valor(new BigDecimal("65.00"))
                        .data(LocalDate.now().minusDays(1))
                        .tipo(Transacao.TipoTransacao.SAIDA)
                        .categoria(categoriaLazer.get())
                        .usuario(joao)
                        .build());
            }

            System.out.println("[OK] 6 Transações exemplo criadas para João!");
        }
    }

    private void criarMetasExemplo() {
        if (metaRepository.count() == 0) {
            var joao = usuarioRepository.findByEmailAndAtivoTrue("joao@nucleo.com").orElseThrow();
            var categoriaLazer = categoriaRepository.findByNome("Lazer");
            var categoriaMoradia = categoriaRepository.findByNome("Moradia");

            if (categoriaLazer.isPresent()) {
                metaRepository.save(Meta.builder()
                        .titulo("Viagem para Europa")
                        .valorAlvo(new BigDecimal("15000.00"))
                        .dataLimite(LocalDate.now().plusMonths(8))
                        .categoriaId(categoriaLazer.get().getId())
                        .usuarioId(joao.getId())
                        .status(StatusMeta.ativa)
                        .build());
            }

            if (categoriaMoradia.isPresent()) {
                metaRepository.save(Meta.builder()
                        .titulo("Comprar Carro")
                        .valorAlvo(new BigDecimal("50000.00"))
                        .dataLimite(LocalDate.now().plusYears(1).plusMonths(2))
                        .categoriaId(categoriaMoradia.get().getId())
                        .usuarioId(joao.getId())
                        .status(StatusMeta.ativa)
                        .build());

                metaRepository.save(Meta.builder()
                        .titulo("Reserva de Emergência")
                        .valorAlvo(new BigDecimal("10000.00"))
                        .dataLimite(LocalDate.now().plusMonths(6))
                        .categoriaId(categoriaMoradia.get().getId())
                        .usuarioId(joao.getId())
                        .status(StatusMeta.ativa)
                        .build());
            }

            System.out.println("[OK] 3 Metas exemplo criadas para João!");
        }
    }
}