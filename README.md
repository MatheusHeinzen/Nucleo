# 💰 Nucleo Backend - Sistema de Gestão Financeira

API REST completa para gerenciamento de finanças pessoais desenvolvida com Spring Boot 3.5.5.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.5**
  - Spring Data JPA (Hibernate)
  - Spring Security + JWT
  - Spring Web
- **H2 Database** (desenvolvimento)
- **MySQL** (opcional - produção)
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)

## 📋 Funcionalidades

- ✅ Autenticação JWT (login/registro)
- ✅ Gerenciamento de transações financeiras (receitas/despesas)
- ✅ Categorização de transações
- ✅ Cálculo automático de saldo
- ✅ Filtros por período, categoria e tipo
- ✅ Metas financeiras
- ✅ Sistema de usuários com roles
- ✅ Soft delete em todas entidades
- ✅ Auditoria automática (datas de criação/atualização)

## 🏗️ Arquitetura

```
Controller (API REST)
    ↓
Service (Lógica de Negócio)
    ↓
Repository (Acesso a Dados)
    ↓
Database (H2/MySQL)
```

**Padrão de herança genérica:**
- `BaseEntity` → Entidades comuns (id, dataCriacao, dataAtualizacao, ativo)
- `BaseRepository` → Operações CRUD + soft delete
- `BaseService` → Lógica comum reutilizável

## 📦 Estrutura do Projeto

```
src/main/java/com/nucleo/
├── config/              # Configurações (DataLoader, OpenAPI)
├── controller/          # Endpoints REST
├── dto/                 # Data Transfer Objects
├── model/               # Entidades JPA
├── repository/          # Acesso a dados
├── security/            # JWT e configurações de segurança
└── service/             # Lógica de negócio
```

## 🔑 Modelos Principais

### Usuario
- Email único
- Senha criptografada (BCrypt)
- Roles: `ROLE_USER`, `ROLE_ADMIN`

### Categoria
- Nome único (global no sistema)
- Tipo: `ENTRADA` ou `SAIDA`
- Descrição

### Transacao
- Descrição, valor (BigDecimal), data
- Tipo: `ENTRADA` ou `SAIDA`
- Relacionada a Usuario e Categoria

### Meta
- Título, valor alvo, data limite
- Status: `ativa`, `concluida`, `cancelada`
- Opcional: vinculada a categoria

## 🔐 Segurança

- **JWT (JSON Web Token)** para autenticação stateless
- Tokens válidos por **24 horas**
- Senhas criptografadas com **BCrypt**
- Endpoints públicos: `/api/auth/**`, `/swagger-ui/**`, `/h2-console/**`
- Demais endpoints: **autenticação obrigatória**

## ⚡ Início Rápido

### Pré-requisitos
- Java 17+
- Maven 3.6+

### Executar

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd backend

# Compile e execute
./mvnw spring-boot:run
```

### Acesso
- **API:** http://localhost:8077
- **Swagger:** http://localhost:8077/swagger-ui.html
- **H2 Console:** http://localhost:8077/h2-console
  - URL: `jdbc:h2:mem:testdb`
  - User: `sa`
  - Password: (vazio)

## 📝 Dados Iniciais

Ao iniciar, o sistema cria automaticamente:

**Usuário Admin:**
- Email: `admin@nucleo.com`
- Senha: `123456`

**9 Categorias Padrão:**
- Saída: ALIMENTACAO, TRANSPORTE, MORADIA, LAZER, SAUDE, EDUCACAO
- Entrada: SALARIO, INVESTIMENTOS, OUTROS

**3 Transações de Exemplo**

## 🔌 Exemplos de Uso

### 1. Login

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

### 2. Criar Transação

```bash
curl -X POST http://localhost:8077/api/transacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu-token}" \
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
  -H "Authorization: Bearer {seu-token}"
```

**Response:**
```json
2704.50
```

### 4. Buscar por Período

```bash
curl -X GET "http://localhost:8077/api/transacoes/usuario/1/periodo?inicio=2025-10-01&fim=2025-10-31" \
  -H "Authorization: Bearer {seu-token}"
```

## 📡 Principais Endpoints

### Autenticação (`/api/auth`)
- `POST /login` - Login
- `POST /registrar` - Registro de novo usuário

### Transações (`/api/transacoes`)
- `POST /` - Criar transação
- `PUT /{id}` - Atualizar transação
- `DELETE /{id}` - Excluir transação
- `GET /usuario/{id}` - Listar por usuário
- `GET /usuario/{id}/periodo` - Filtrar por período
- `GET /usuario/{id}/categoria/{categoriaId}` - Filtrar por categoria
- `GET /usuario/{id}/tipo/{tipo}` - Filtrar por tipo (ENTRADA/SAIDA)
- `GET /usuario/{id}/saldo` - Consultar saldo
- `GET /usuario/{id}/resumo` - Resumo financeiro

### Categorias (`/api/categorias`)
- CRUD completo: GET, POST, PUT, DELETE

### Metas (`/api/metas`)
- CRUD completo: GET, POST, PUT, DELETE

### Usuários (`/api/usuarios`)
- CRUD completo: GET, PUT, DELETE

## ⚙️ Configuração

### application.properties

```properties
# Porta do servidor
server.port=8077

# Banco de dados H2 (desenvolvimento)
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# JWT
jwt.secret=mySuperSecretKeyThatIsVeryLongAndSecureForJWTTokenGeneration12345
jwt.expiration=86400000  # 24 horas

# JPA
spring.jpa.hibernate.ddl-auto=create-drop  # Recria schema na inicialização
spring.jpa.show-sql=true
```

## 🎯 Padrões Utilizados

- **Clean Code** - Código limpo e legível
- **SOLID** - Princípios de design orientado a objetos
- **DRY** - Não repetir código (herança genérica)
- **Arquitetura em Camadas** - Separação de responsabilidades
- **DTO Pattern** - Transferência de dados desacoplada
- **Repository Pattern** - Acesso a dados abstraído
- **Soft Delete** - Exclusão lógica (mantém histórico)

## 🔍 Recursos Especiais

### Soft Delete
Registros nunca são excluídos fisicamente. O campo `ativo` é marcado como `false`:
```java
// Ao buscar, apenas registros ativos são retornados
repository.findAllByAtivoTrue();
```

### Auditoria Automática
Campos gerenciados automaticamente:
```java
@CreatedDate
private LocalDateTime dataCriacao;  // Preenchida na criação

@LastModifiedDate
private LocalDateTime dataAtualizacao;  // Atualizada nas modificações
```

### Queries Personalizadas
Soma de valores por tipo de transação:
```java
@Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t 
        WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo AND t.ativo = true")
BigDecimal sumValorByUsuarioIdAndTipo(@Param("usuarioId") Long id, 
                                       @Param("tipo") TipoTransacao tipo);
```

## 📚 Documentação Completa

Para documentação detalhada com explicações sobre arquitetura, fluxos, queries personalizadas e prompt para IA, consulte:

📄 **[DOCUMENTACAO_COMPLETA.md](DOCUMENTACAO_COMPLETA.md)**

## 🐛 Troubleshooting

### Porta 8077 já está em uso
Altere a porta no `application.properties`:
```properties
server.port=8080
```

### Erro ao conectar H2 Console
Verifique se a URL está correta: `jdbc:h2:mem:testdb`

### Token JWT expirado
Faça login novamente para obter novo token. Tokens são válidos por 24h.

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é um exemplo educacional. Adapte e use conforme necessário.

## 📞 Suporte

- **Swagger UI:** Teste interativo dos endpoints
- **H2 Console:** Visualize e consulte o banco de dados
- **Logs:** Habilitados com `spring.jpa.show-sql=true`

---

**Desenvolvido com ☕ Java e ❤️ Spring Boot**

**Versão:** 0.0.1-SNAPSHOT | **Última atualização:** 07/10/2025

