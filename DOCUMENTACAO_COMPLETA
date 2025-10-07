# Documentação Completa - Sistema Nucleo Backend

## 📋 Visão Geral

O **Nucleo Backend** é uma API REST desenvolvida em **Spring Boot 3.5.5** para gerenciamento de finanças pessoais. O sistema permite que usuários registrem transações financeiras (entradas e saídas), categorizem seus gastos, definam metas financeiras e acompanhem seu saldo.

**Tecnologias principais:**
- Java 17
- Spring Boot 3.5.5
- Spring Data JPA (Hibernate)
- Spring Security com JWT
- H2 Database (desenvolvimento)
- MySQL (opcional para produção)
- Lombok
- SpringDoc OpenAPI (Swagger)

---

## 🏗️ Arquitetura e Estrutura

### Padrão de Arquitetura
O projeto segue uma **arquitetura em camadas** (Layered Architecture) com separação clara de responsabilidades:

```
Controller → Service → Repository → Database
     ↓          ↓
   DTO      Entity/Model
```

### Estrutura de Pastas

```
src/main/java/com/nucleo/
├── BackendApplication.java          # Classe principal
├── config/                          # Configurações do sistema
│   ├── DataLoader.java             # Dados iniciais (seed)
│   └── OpenAPIConfig.java          # Configuração Swagger
├── controller/                      # Controllers REST
│   ├── AuthController.java         # Autenticação (login/registro)
│   ├── CategoriaController.java    # CRUD de categorias
│   ├── MetaController.java         # CRUD de metas
│   ├── TransacaoController.java    # CRUD e operações de transações
│   └── UsuarioController.java      # CRUD de usuários
├── dto/                            # Data Transfer Objects
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── GenericResponse.java
│   ├── TransacaoRequest.java
│   └── TransacaoResponse.java
├── model/                          # Entidades JPA
│   ├── base/
│   │   └── BaseEntity.java        # Entidade base com campos comuns
│   ├── Categoria.java
│   ├── Meta.java
│   ├── StatusMeta.java
│   ├── Transacao.java
│   └── Usuario.java
├── repository/                     # Repositórios JPA
│   ├── generic/
│   │   └── BaseRepository.java    # Repository genérico com soft delete
│   ├── CategoriaRepository.java
│   ├── MetaRepository.java
│   ├── TransacaoRepository.java
│   └── UsuarioRepository.java
├── security/                       # Segurança e JWT
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── SecurityConfig.java
│   ├── SecurityUtils.java
│   └── UserDetailsServiceImpl.java
└── service/                        # Lógica de negócio
    ├── generic/
    │   └── BaseService.java       # Service genérico
    ├── AuthService.java
    ├── CategoriaService.java
    ├── MetaService.java
    └── TransacaoService.java
```

---

## 🔑 Modelos de Dados (Entities)

### BaseEntity (Classe Base)
Entidade abstrata que fornece campos comuns para todas as entidades:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    private LocalDateTime dataCriacao;
    
    @LastModifiedDate
    private LocalDateTime dataAtualizacao;
    
    @Column(nullable = false)
    private Boolean ativo = true;  // Para soft delete
}
```

**Características:**
- Auto incremento no ID
- Auditoria automática (data de criação e atualização)
- Soft delete (campo `ativo` em vez de excluir fisicamente)

### Usuario

```java
@Entity
@Table(name = "usuarios")
public class Usuario extends BaseEntity {
    private String nome;
    private String email;
    private String senha;  // Hash BCrypt
    
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles = Set.of(Role.ROLE_USER);
    
    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }
}
```

**Características:**
- Email único
- Senha criptografada com BCrypt
- Sistema de roles (USER/ADMIN)
- Herda soft delete da BaseEntity

### Categoria

```java
@Entity
@Table(name = "categorias")
public class Categoria extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String nome;
    
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    private TipoCategoria tipo;  // ENTRADA ou SAIDA
    
    public enum TipoCategoria {
        ENTRADA, SAIDA
    }
}
```

**Características:**
- Nome único no sistema (categorias globais)
- Tipo define se é categoria de entrada ou saída
- Categorias padrão criadas no DataLoader

### Transacao

```java
@Entity
@Table(name = "transacoes")
public class Transacao extends BaseEntity {
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(nullable = false)
    private LocalDate data;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;  // ENTRADA, SAIDA
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    public enum TipoTransacao {
        ENTRADA, SAIDA
    }
}
```

**Características:**
- BigDecimal para valores monetários (precisão)
- Relacionamento ManyToOne com Usuario
- Relacionamento ManyToOne com Categoria
- Tipo de transação (ENTRADA/SAIDA)

### Meta

```java
@Entity
@Table(name = "metas")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(nullable = false, length = 120)
    private String titulo;
    
    @Column(name = "valor_alvo", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorAlvo;
    
    @Column(name = "data_limite", nullable = false)
    private LocalDate dataLimite;
    
    @Column(name = "categoria_id")
    private Long categoriaId;  // Opcional
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMeta status = StatusMeta.ativa;
}
```

**Características:**
- Metas de economia/gastos
- Status: ativa, concluida, cancelada
- Pode estar vinculada a uma categoria (opcional)

---

## 🔐 Sistema de Segurança

### Autenticação JWT

O sistema usa **JWT (JSON Web Token)** para autenticação stateless:

1. **Login/Registro**: Usuário envia email e senha
2. **Token gerado**: Sistema gera JWT válido por 24h (86400000ms)
3. **Autenticação**: Cliente envia token no header `Authorization: Bearer {token}`
4. **Validação**: Filtro valida token em cada requisição

### Componentes de Segurança

#### SecurityConfig
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Configuração da cadeia de filtros
    // Endpoints públicos: /api/auth/**, /swagger-ui/**, /h2-console/**
    // Demais endpoints: autenticação obrigatória
    // Política de sessão: STATELESS (sem sessão HTTP)
}
```

#### JwtTokenProvider
```java
@Component
public class JwtTokenProvider {
    // Gera tokens JWT com roles do usuário
    // Valida tokens
    // Extrai username do token
    // Chave secreta: jwt.secret
    // Expiração: jwt.expiration (24h padrão)
}
```

#### JwtAuthenticationFilter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Intercepta requisições HTTP
    // Extrai token do header Authorization
    // Valida token e autentica usuário no contexto Spring Security
}
```

### Endpoints Públicos (sem autenticação)
- `POST /api/auth/login` - Login
- `POST /api/auth/registrar` - Registro de novo usuário
- `/swagger-ui/**` - Documentação API
- `/h2-console/**` - Console H2 Database

### Endpoints Protegidos
Todos os demais endpoints requerem autenticação via JWT.

---

## 📡 API Endpoints

### 🔓 Autenticação (`/api/auth`)

#### POST `/api/auth/login`
Login de usuário.

**Request:**
```json
{
  "email": "admin@nucleo.com",
  "senha": "123456"
}
```

**Response 200:**
```json
{
  "token": "Bearer eyJhbGciOiJIUzUxMiJ9...",
  "email": "admin@nucleo.com",
  "roles": ["ROLE_ADMIN"]
}
```

#### POST `/api/auth/registrar`
Registra novo usuário e retorna token automaticamente.

**Request:**
```json
{
  "email": "novo@email.com",
  "senha": "senha123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "novo@email.com",
  "roles": ["ROLE_USER"]
}
```

---

### 💰 Transações (`/api/transacoes`)

#### POST `/api/transacoes`
Cria nova transação.

**Request:**
```json
{
  "descricao": "Supermercado",
  "valor": 250.50,
  "data": "2025-10-07",
  "tipo": "SAIDA",
  "categoriaId": 1,
  "usuarioId": 1
}
```

**Response 200:**
```json
{
  "id": 1,
  "descricao": "Supermercado",
  "valor": 250.50,
  "data": "2025-10-07",
  "tipo": "SAIDA",
  "categoriaId": 1,
  "categoriaNome": "ALIMENTACAO",
  "usuarioId": 1,
  "usuarioNome": "Administrador",
  "dataCriacao": "2025-10-07T10:30:00",
  "dataAtualizacao": null
}
```

#### PUT `/api/transacoes/{id}`
Atualiza transação existente.

**Request:** Mesmo formato do POST

#### DELETE `/api/transacoes/{id}`
Exclui transação (soft delete).

**Response:** 204 No Content

#### GET `/api/transacoes/usuario/{usuarioId}`
Lista todas as transações de um usuário.

**Response 200:**
```json
[
  {
    "id": 1,
    "descricao": "Salário",
    "valor": 3000.00,
    "tipo": "ENTRADA",
    ...
  }
]
```

#### GET `/api/transacoes/usuario/{usuarioId}/periodo`
Busca transações por período.

**Query Params:**
- `inicio`: Data inicial (formato: YYYY-MM-DD)
- `fim`: Data final (formato: YYYY-MM-DD)

**Exemplo:** `/api/transacoes/usuario/1/periodo?inicio=2025-10-01&fim=2025-10-31`

#### GET `/api/transacoes/usuario/{usuarioId}/categoria/{categoriaId}`
Busca transações por categoria específica.

#### GET `/api/transacoes/usuario/{usuarioId}/tipo/{tipo}`
Busca transações por tipo (ENTRADA ou SAIDA).

**Exemplo:** `/api/transacoes/usuario/1/tipo/ENTRADA`

#### GET `/api/transacoes/usuario/{usuarioId}/saldo`
Retorna saldo atual do usuário (entradas - saídas).

**Response 200:**
```json
2704.50
```

#### GET `/api/transacoes/usuario/{usuarioId}/resumo`
Retorna resumo financeiro do usuário.

**Response 200:**
```json
"Entradas: R$ 3000.00 | Saídas: R$ 295.50 | Saldo: R$ 2704.50"
```

---

### 📂 Categorias (`/api/categorias`)

**Operações CRUD padrão:**
- `GET /api/categorias` - Lista todas
- `GET /api/categorias/{id}` - Busca por ID
- `POST /api/categorias` - Cria nova
- `PUT /api/categorias/{id}` - Atualiza
- `DELETE /api/categorias/{id}` - Exclui (soft delete)

**Categorias Padrão:**
- **SAIDA**: ALIMENTACAO, TRANSPORTE, MORADIA, LAZER, SAUDE, EDUCACAO
- **ENTRADA**: SALARIO, INVESTIMENTOS, OUTROS

---

### 🎯 Metas (`/api/metas`)

**Operações CRUD padrão:**
- `GET /api/metas` - Lista todas
- `GET /api/metas/{id}` - Busca por ID
- `POST /api/metas` - Cria nova
- `PUT /api/metas/{id}` - Atualiza
- `DELETE /api/metas/{id}` - Exclui

---

### 👤 Usuários (`/api/usuarios`)

**Operações CRUD padrão:**
- `GET /api/usuarios` - Lista todos
- `GET /api/usuarios/{id}` - Busca por ID
- `PUT /api/usuarios/{id}` - Atualiza
- `DELETE /api/usuarios/{id}` - Exclui (soft delete)

---

## 🏛️ Padrão de Design Genérico

O sistema implementa um **padrão de herança genérica** para reduzir duplicação de código.

### BaseRepository

```java
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {
    Optional<T> findByIdAndAtivoTrue(ID id);
    List<T> findAllByAtivoTrue();
    Page<T> findAllByAtivoTrue(Pageable pageable);
    void softDelete(@Param("id") ID id);
    boolean existsByIdAndAtivoTrue(ID id);
}
```

**Funcionalidades:**
- Operações CRUD básicas
- Soft delete automático
- Filtros por campo `ativo`
- Suporte a paginação

### BaseService

```java
@RequiredArgsConstructor
public abstract class BaseService<T extends BaseEntity, ID, R extends BaseRepository<T, ID>> {
    protected final R repository;
    
    public Optional<T> findById(ID id);
    public List<T> findAll();
    public Page<T> findAll(Pageable pageable);
    public T save(T entity);
    public void delete(ID id);
    public boolean exists(ID id);
}
```

**Vantagens:**
- Métodos comuns implementados uma única vez
- Services específicos herdam e adicionam lógica especializada
- Exemplo: `TransacaoService extends BaseService<Transacao, Long, TransacaoRepository>`

---

## 🗄️ Banco de Dados

### Configuração (application.properties)

```properties
# H2 Database (em memória)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=create-drop  # Recria schema a cada inicialização

# JWT
jwt.secret=mySuperSecretKeyThatIsVeryLongAndSecureForJWTTokenGeneration12345
jwt.expiration=86400000  # 24 horas

# Servidor
server.port=8077
```

### DataLoader - Dados Iniciais

O `DataLoader` executa na inicialização e cria:

**Usuário Admin:**
- Email: `admin@nucleo.com`
- Senha: `123456`
- Roles: `ROLE_ADMIN`

**9 Categorias Padrão** (alimentação, transporte, moradia, etc.)

**3 Transações de Exemplo** (salário, supermercado, cinema)

---

## 🔄 Fluxo de Funcionamento

### 1. Inicialização
```
BackendApplication.main()
  → Spring Boot inicializa
  → DataLoader.run() cria dados iniciais
  → Servidor disponível na porta 8077
```

### 2. Registro de Usuário
```
POST /api/auth/registrar
  → AuthController recebe dados
  → AuthService valida email único
  → Senha criptografada com BCrypt
  → Usuario salvo no banco
  → JWT gerado e retornado
```

### 3. Login
```
POST /api/auth/login
  → AuthController recebe credenciais
  → AuthService valida com AuthenticationManager
  → JWT gerado com roles do usuário
  → Token retornado no response
```

### 4. Criação de Transação
```
POST /api/transacoes (Header: Authorization: Bearer {token})
  → JwtAuthenticationFilter valida token
  → Usuario autenticado no contexto
  → TransacaoController recebe request
  → Valida Usuario e Categoria existentes
  → TransacaoService.save() persiste
  → TransacaoResponse retornado
```

### 5. Consulta de Saldo
```
GET /api/transacoes/usuario/{id}/saldo
  → TransacaoController.getSaldo()
  → TransacaoService.getSaldo()
  → Repository soma ENTRADAS (Query)
  → Repository soma SAIDAS (Query)
  → Calcula: entradas - saidas
  → Retorna BigDecimal
```

---

## 📊 Queries Personalizadas

### Soma de Valores por Tipo (TransacaoRepository)

```java
@Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t 
        WHERE t.usuario.id = :usuarioId 
        AND t.tipo = :tipo 
        AND t.ativo = true")
BigDecimal sumValorByUsuarioIdAndTipo(
    @Param("usuarioId") Long usuarioId, 
    @Param("tipo") TipoTransacao tipo
);
```

**Uso:**
```java
BigDecimal totalEntradas = repository.sumValorByUsuarioIdAndTipo(1L, ENTRADA);
BigDecimal totalSaidas = repository.sumValorByUsuarioIdAndTipo(1L, SAIDA);
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 17+
- Maven 3.6+

### Passos

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd backend
```

2. **Compile o projeto**
```bash
mvnw clean install
```

3. **Execute a aplicação**
```bash
mvnw spring-boot:run
```

4. **Acesse:**
- API: `http://localhost:8077`
- Swagger: `http://localhost:8077/swagger-ui.html`
- H2 Console: `http://localhost:8077/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (deixar vazio)

---

## 📝 Testando a API

### 1. Fazer Login
```bash
curl -X POST http://localhost:8077/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@nucleo.com","senha":"123456"}'
```

**Response:**
```json
{
  "token": "Bearer eyJhbGciOiJIUzUxMiJ9...",
  "email": "admin@nucleo.com",
  "roles": ["ROLE_ADMIN"]
}
```

### 2. Criar Transação (use o token acima)
```bash
curl -X POST http://localhost:8077/api/transacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "descricao": "Almoço",
    "valor": 35.00,
    "data": "2025-10-07",
    "tipo": "SAIDA",
    "categoriaId": 1,
    "usuarioId": 1
  }'
```

### 3. Consultar Saldo
```bash
curl -X GET http://localhost:8077/api/transacoes/usuario/1/saldo \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## 🛠️ Tecnologias e Dependências

### Core
- **Spring Boot 3.5.5**: Framework principal
- **Spring Data JPA**: Persistência e ORM
- **Spring Security**: Autenticação e autorização
- **Spring Web**: API REST

### Banco de Dados
- **H2 Database**: Banco em memória (desenvolvimento)
- **MySQL Connector 9.4.0**: Suporte a MySQL (produção)

### Segurança
- **JJWT 0.13.0**: Geração e validação de tokens JWT

### Utilitários
- **Lombok**: Redução de boilerplate
- **SpringDoc OpenAPI 2.8.6**: Documentação Swagger automática
- **Spring Boot DevTools**: Hot reload em desenvolvimento

---

## 🔍 Recursos Avançados

### Soft Delete
Todas as entidades que herdam `BaseEntity` possuem soft delete:
- Ao excluir, apenas marca `ativo = false`
- Queries filtram automaticamente por `ativo = true`
- Dados nunca são perdidos

### Auditoria Automática
Campos gerenciados automaticamente pelo JPA:
- `dataCriacao`: Preenchida ao criar
- `dataAtualizacao`: Atualizada em cada modificação

### Paginação
Todos os services suportam paginação:
```java
Page<Transacao> page = transacaoService.findAll(
    PageRequest.of(0, 10, Sort.by("data").descending())
);
```

### Lazy Loading
Relacionamentos usam `FetchType.LAZY` para performance:
```java
@ManyToOne(fetch = FetchType.LAZY)
private Usuario usuario;
```

---

## 🎯 Casos de Uso Principais

### 1. Gerenciamento de Finanças Pessoais
- Registrar receitas e despesas
- Categorizar gastos
- Acompanhar saldo em tempo real

### 2. Análise Financeira
- Filtrar transações por período
- Agrupar por categoria
- Gerar resumos (entradas, saídas, saldo)

### 3. Metas de Economia
- Definir objetivos financeiros
- Estabelecer prazos
- Vincular a categorias específicas

### 4. Controle Multi-usuário
- Sistema de autenticação seguro
- Dados isolados por usuário
- Roles para permissões (USER/ADMIN)

---

## ⚙️ Configurações Importantes

### Alterar Porta do Servidor
```properties
server.port=8080  # Altere para a porta desejada
```

### Configurar MySQL (Produção)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nucleo
spring.datasource.username=root
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update  # Não recria schema
```

### Ajustar Tempo de Expiração JWT
```properties
jwt.expiration=3600000  # 1 hora (em milissegundos)
```

### Mudar Secret do JWT (IMPORTANTE em produção)
```properties
jwt.secret=SuaChaveSecretaMuitoSeguraComPeloMenos256Bits
```

---

## 📚 Padrões e Boas Práticas

### Clean Code
- Métodos pequenos e focados
- Nomenclatura clara e descritiva
- Evita duplicação (DRY)

### SOLID
- **Single Responsibility**: Cada classe tem um propósito único
- **Open/Closed**: Herança genérica permite extensão sem modificação
- **Liskov Substitution**: BaseService/BaseRepository podem ser substituídos
- **Interface Segregation**: Repositories específicos para necessidades específicas
- **Dependency Inversion**: Injeção de dependências com @RequiredArgsConstructor

### Arquitetura em Camadas
- Controllers: Exposição HTTP
- Services: Lógica de negócio
- Repositories: Acesso a dados
- DTOs: Transferência de dados

---

## 🔐 Segurança

### Boas Práticas Implementadas
- ✅ Senhas criptografadas (BCrypt)
- ✅ Tokens JWT assinados (HS512)
- ✅ Autenticação stateless
- ✅ CSRF desabilitado (stateless API)
- ✅ Endpoints públicos mínimos

### Recomendações para Produção
- [ ] Usar HTTPS
- [ ] Secret JWT em variável de ambiente
- [ ] Senha do banco em variável de ambiente
- [ ] Rate limiting
- [ ] Logs de auditoria
- [ ] Validação de inputs (Bean Validation)

---

## 🐛 Tratamento de Erros

Atualmente o tratamento é básico:
```java
try {
    // Operação
} catch (Exception e) {
    return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
}
```

**Sugestão de melhoria**: Implementar `@ControllerAdvice` para tratamento centralizado.

---

## 📈 Melhorias Futuras

### Funcionalidades
- [ ] Relatórios em PDF
- [ ] Gráficos de gastos por categoria
- [ ] Notificações de metas próximas ao prazo
- [ ] Importação de extratos bancários (OFX/CSV)
- [ ] Orçamento mensal por categoria
- [ ] Compartilhamento de categorias entre usuários

### Técnicas
- [ ] Cache com Redis
- [ ] Testes unitários e de integração
- [ ] Containerização (Docker)
- [ ] CI/CD (GitHub Actions)
- [ ] Monitoramento (Actuator + Prometheus)
- [ ] Migrations (Flyway/Liquibase)

---

## 🤖 Prompt para IA Gerar Código Similar

Ao solicitar que uma IA gere código para este projeto, use o seguinte prompt:

---

**CONTEXTO:**

Você está trabalhando em um projeto Spring Boot 3.5.5 com Java 17 chamado "Nucleo Backend". É uma API REST de gerenciamento financeiro pessoal.

**ARQUITETURA:**
- Padrão em camadas: Controller → Service → Repository → Entity
- Herança genérica: BaseEntity, BaseRepository, BaseService
- Soft delete: Campo `ativo` em todas entidades
- Auditoria automática: `dataCriacao`, `dataAtualizacao`

**ENTIDADES PRINCIPAIS:**
1. **Usuario**: nome, email (único), senha (BCrypt), roles (ROLE_USER/ROLE_ADMIN)
2. **Categoria**: nome (único), descricao, tipo (ENTRADA/SAIDA)
3. **Transacao**: descricao, valor (BigDecimal), data, tipo (ENTRADA/SAIDA), relacionamentos ManyToOne com Usuario e Categoria
4. **Meta**: titulo, valorAlvo, dataLimite, status (ativa/concluida/cancelada), usuarioId, categoriaId (opcional)

**SEGURANÇA:**
- JWT (JSON Web Token) com expiração de 24h
- JwtTokenProvider: gera e valida tokens
- JwtAuthenticationFilter: intercepta requisições e valida token
- Endpoints públicos: `/api/auth/**`, `/swagger-ui/**`, `/h2-console/**`
- Todos os demais endpoints requerem autenticação

**TECNOLOGIAS:**
- Spring Boot 3.5.5
- Spring Data JPA (Hibernate)
- Spring Security
- H2 Database (desenvolvimento)
- Lombok
- SpringDoc OpenAPI

**CONVENÇÕES:**
- Usar Lombok (@Data, @Builder, @RequiredArgsConstructor, @Getter, @Setter)
- DTOs separados para Request e Response
- Métodos de repositório seguem padrão Spring Data: `findAllBy{campo}And{campo}AndAtivoTrue`
- Services herdam de BaseService e adicionam métodos específicos
- Controllers retornam `ResponseEntity<?>`
- Nomear campos em português (dataCriacao, valorAlvo, etc.)

**ESTRUTURA DE PACOTES:**
```
com.nucleo
├── config
├── controller
├── dto
├── model (entities)
│   └── base
├── repository
│   └── generic
├── security
└── service
    └── generic
```

**DADOS INICIAIS (DataLoader):**
- Usuario admin: admin@nucleo.com / 123456
- 9 categorias padrão (ALIMENTACAO, TRANSPORTE, SALARIO, etc.)
- 3 transações de exemplo

**EXEMPLO DE CÓDIGO:**

```java
// Entity
@Entity
@Table(name = "transacoes")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Transacao extends BaseEntity {
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}

// Service
@Service
public class TransacaoService extends BaseService<Transacao, Long, TransacaoRepository> {
    public TransacaoService(TransacaoRepository repository) {
        super(repository);
    }
    
    public List<Transacao> findByUsuarioId(Long usuarioId) {
        return repository.findAllByUsuarioIdAndAtivoTrue(usuarioId);
    }
}

// Controller
@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {
    private final TransacaoService service;
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponse>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(/* ... */);
    }
}
```

**TAREFA:**
[Descrever aqui o que você quer que a IA gere, por exemplo:]
- "Crie um novo controller para relatórios financeiros"
- "Adicione endpoint para buscar transações recorrentes"
- "Implemente service para calcular média de gastos por categoria"

---

## 📞 Suporte e Documentação

### Swagger UI
Acesse `http://localhost:8077/swagger-ui.html` para:
- Visualizar todos os endpoints
- Testar requisições interativamente
- Ver schemas de request/response

### H2 Console
Acesse `http://localhost:8077/h2-console` para:
- Visualizar tabelas e dados
- Executar queries SQL
- Debugar problemas de persistência

---

## 📄 Licença

Este projeto é um exemplo educacional. Adapte conforme necessário.


---

## 🎓 Resumo para IA

Use este resumo conciso quando precisar de contexto rápido:

**Sistema:** API REST de gestão financeira em Spring Boot 3.5.5
**Stack:** Java 17, JPA/Hibernate, Spring Security, JWT, H2/MySQL
**Arquitetura:** Camadas (Controller/Service/Repository) + Herança genérica
**Entidades:** Usuario, Categoria, Transacao, Meta (todas com soft delete)
**Segurança:** JWT stateless, BCrypt, roles (USER/ADMIN)
**Features:** CRUD completo, filtros (período, categoria, tipo), cálculos (saldo, totais), auditoria automática
**Endpoints principais:** /api/auth (login/registro), /api/transacoes (CRUD + consultas), /api/categorias, /api/metas, /api/usuarios
**Dados iniciais:** Admin (admin@nucleo.com/123456), 9 categorias, 3 transações exemplo
**Porta:** 8077 | **Console H2:** /h2-console | **Swagger:** /swagger-ui.html

---

**Última atualização:** 07/10/2025
**Versão:** 0.0.1-SNAPSHOT

