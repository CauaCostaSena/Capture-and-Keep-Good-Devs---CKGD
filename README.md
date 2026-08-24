# CKGD — Capture and Keep Good Devs

Plataforma web para empresas buscarem desenvolvedores com base em dados técnicos
reais e públicos do GitHub (repositórios, linguagens, atividade), sem depender de
cadastro manual de candidatos.

Este repositório contém o projeto completo: banco de dados, backend (API REST) e
frontend.

---

## Stack

- **Banco de dados:** MySQL 8+
- **Backend:** Java 21 + Spring Boot 3 (Web, Data JPA, Security, Validation) + JWT
- **Frontend:** HTML, CSS e JavaScript puro (sem framework), consumindo a API via `fetch`
- **Integração externa:** API pública do GitHub (busca de usuários, perfis e repositórios)

---

## Estrutura do repositório

```
├── database/                  # Scripts SQL (execute nesta ordem)
│   ├── 01_schema.sql          # Criação das tabelas, PKs, FKs, índices
│   ├── 02_views_routines.sql  # Views, stored procedures e function
│   └── 03_data_manipulation.sql  # Dados de exemplo + consultas de validação
│
├── backend/                   # API REST em Spring Boot
│   ├── pom.xml
│   └── src/main/java/com/ckgd/
│       ├── entity/             # Entidades JPA (mapeiam as tabelas do banco)
│       ├── repository/         # Spring Data JPA
│       ├── service/            # Regras de negócio (empresa, busca, GitHub, favoritos)
│       ├── controller/         # Endpoints REST
│       ├── security/           # JWT (filtro, geração/validação de token)
│       ├── config/             # Security, CORS, cliente HTTP do GitHub, seed de planos
│       ├── dto/                 # Objetos de entrada/saída da API
│       └── exception/           # Tratamento de erros
│
├── index.html, cadastro.html, home.html,
│   perfil.html, favoritos.html, config.html   # Telas do frontend
├── css/                                        # Estilos
├── js/                                         # Lógica do frontend (api.js + 1 arquivo por tela)
└── images/
```

---

## Como rodar

### 1. Banco de dados

Com o MySQL rodando localmente:

```bash
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_views_routines.sql
mysql -u root -p < database/03_data_manipulation.sql   # opcional: dados de exemplo
```

Isso cria o schema `ckgd` com todas as tabelas, views, procedures e (opcionalmente)
alguns registros de exemplo para você testar consultas diretamente no banco.

> O backend também popula automaticamente os planos de assinatura (Free/Pro/Enterprise)
> na primeira inicialização caso a tabela esteja vazia — então mesmo sem rodar o passo
> `03_data_manipulation.sql`, o cadastro de empresas funciona normalmente.

### 2. Backend

Pré-requisitos: **Java 21** e **Maven** instalados.

Por padrão, o backend espera usuário `root` **sem senha**. Se o seu MySQL local
tiver senha, defina-a via variável de ambiente (não edite o `application.properties`
com a senha em texto puro, para não versioná-la no Git):

```bash
CKGD_DB_PASSWORD=sua_senha mvn spring-boot:run
```

Se o usuário também for diferente de `root`, aí sim edite `spring.datasource.username`
em `backend/src/main/resources/application.properties`.

Rode:

```bash
cd backend
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

**Recomendado:** configure um token do GitHub para evitar o limite de 60
requisições/hora do acesso anônimo (com token, o limite sobe para 5.000/hora):

1. Gere um token em https://github.com/settings/tokens (não precisa de nenhum escopo,
   apenas o acesso de leitura pública padrão)
2. Rode o backend com a variável de ambiente:
   ```bash
   CKGD_GITHUB_TOKEN=seu_token_aqui mvn spring-boot:run
   ```

### 3. Frontend

Como é HTML/CSS/JS puro, basta servir a pasta raiz do projeto com qualquer
servidor estático. Exemplos:

```bash
# Opção 1: Python
python3 -m http.server 8090

# Opção 2: extensão "Live Server" do VS Code
# clique com o botão direito em index.html → "Open with Live Server"
```

Depois acesse `http://localhost:8090/index.html` (ou a porta escolhida) no navegador.

> O frontend está configurado para chamar a API em `http://localhost:8080/api`
> (veja `js/api.js`, constante `API_BASE_URL`). Ajuste esse valor se o backend
> rodar em outro endereço/porta.

---

## Fluxo de uso

1. **Cadastro** (`cadastro.html`) — a empresa cria a conta escolhendo um plano de assinatura
2. **Login** (`index.html`) — autenticação via e-mail/senha, retorna um token JWT
3. **Busca** (`home.html`) — pesquisa por termo livre, linguagem e localização;
   os resultados vêm ao vivo da API do GitHub e são cacheados no banco local
4. **Perfil do candidato** (`perfil.html`) — detalhes, repositórios, e opção de
   salvar como favorito com uma avaliação privada
5. **Salvos** (`favoritos.html`) — lista os candidatos favoritados pela empresa
6. **Configurações** (`config.html`) — dados da conta e do plano

---

## Endpoints principais da API

| Método | Rota                          | Descrição                                    | Autenticado |
|--------|--------------------------------|-----------------------------------------------|:-----------:|
| POST   | `/api/auth/cadastro`          | Cadastra uma nova empresa                     | não |
| POST   | `/api/auth/login`             | Login, retorna token JWT                      | não |
| GET    | `/api/planos`                 | Lista os planos de assinatura                 | não |
| GET    | `/api/empresas/me`            | Dados da empresa logada                       | sim |
| PUT    | `/api/empresas/me`            | Atualiza nome da empresa e/ou telefone        | sim |
| POST   | `/api/empresas/me/foto`       | Envia/substitui a foto de perfil (multipart, campo `arquivo`) | sim |
| POST   | `/api/auth/redefinir-senha`   | Redefine a senha (verifica CNPJ + e-mail)     | não |
| GET    | `/api/busca?termo=&linguagem=&localizacao=` | Busca candidatos no GitHub       | sim |
| GET    | `/api/candidatos/{nodeId}`    | Perfil completo de um candidato               | sim |
| GET    | `/api/favoritos`              | Lista candidatos favoritados                  | sim |
| PUT    | `/api/favoritos/{nodeId}`     | Favorita/avalia um candidato                  | sim |
| DELETE | `/api/favoritos/{nodeId}`     | Remove um favorito                            | sim |

Rotas autenticadas exigem o header `Authorization: Bearer <token>` obtido no login/cadastro.

---

## Limitações conhecidas / próximos passos

- **Filtros de busca:** a API do GitHub não expõe "senioridade" ou "idade" do
  desenvolvedor (dados que apareciam no protótipo visual original), então os
  filtros implementados são os que a API realmente suporta: **linguagem** e
  **localização**, além do termo de busca livre.
- **Rate limit do GitHub:** sem token configurado, o limite é de 60 requisições/hora.
  Configure `CKGD_GITHUB_TOKEN` (veja seção Backend acima) para uso contínuo.
- Itens da tela de Configurações marcados **"(em breve)"** (alterar senha estando
  logado, 2FA, exportar dados) não têm endpoint implementado nesta versão —
  ficaram fora do escopo definido na documentação original (que os listava como
  parte do CRUD "opcional"). Nome da empresa, telefone e foto de perfil **já são
  editáveis** na tela de Configurações.
- **Recuperação de senha:** implementada via verificação de CNPJ + e-mail
  (`recuperar-senha.html` → `POST /api/auth/redefinir-senha`), sem depender de
  servidor de e-mail. É menos seguro que um link por e-mail com token temporário
  (quem souber o CNPJ e o e-mail da empresa consegue trocar a senha), mas
  suficiente para o escopo deste projeto.
- **Foto de perfil:** armazenada localmente no servidor, em `backend/uploads/`
  (caminho configurável via `ckgd.upload.dir`), servida publicamente em `/uploads/**`.
  Esse diretório é ignorado pelo Git — cada ambiente acumula suas próprias fotos.

---

## Validação feita

- **Banco de dados:** todos os scripts (`01`, `02`, `03`) foram executados de ponta
  a ponta em um MySQL real, incluindo teste de integridade referencial (`ON DELETE
  CASCADE`), views, stored procedure e function.
- **Frontend:** todas as telas foram testadas visualmente e funcionalmente
  (Playwright) contra um servidor que replica exatamente o contrato JSON da API
  real — cadastro, login, busca, filtro por linguagem, favoritar/avaliar candidato,
  listagem de favoritos, configurações e logout com proteção de rota.
- **Backend:** o código foi revisado manualmente (consistência de tipos, nomes de
  métodos derivados do Spring Data, imports, estrutura de pacotes). **Não foi
  possível compilar com Maven** no ambiente onde este projeto foi gerado, por não
  ter acesso ao Maven Central — rode `mvn spring-boot:run` na sua máquina para a
  primeira compilação real. Se aparecer algum erro de compilação, me avise com a
  mensagem completa que eu corrijo.
