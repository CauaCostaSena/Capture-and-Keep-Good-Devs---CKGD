-- ============================================================================
-- CKGD - Capture and Keep Good Devs
-- Script de criação do banco de dados (Modelo Físico)
-- Baseado no Modelo Conceitual e Lógico descritos na documentação do projeto
-- ============================================================================

DROP DATABASE IF EXISTS ckgd;
CREATE DATABASE ckgd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ckgd;

-- ============================================================================
-- TABELA: PLANO_DE_ASSINATURA
-- Define os limites de uso da plataforma para cada empresa
-- ============================================================================
CREATE TABLE plano_de_assinatura (
    id_plano            INT AUTO_INCREMENT PRIMARY KEY,
    nome_plano          VARCHAR(60)     NOT NULL,
    preco_plano         DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    periodicidade       ENUM('MENSAL', 'ANUAL')       NOT NULL DEFAULT 'MENSAL',
    limite_requisicao   INT             NOT NULL DEFAULT 0,   -- limite de buscas
    limite_avaliacao    INT             NOT NULL DEFAULT 0,   -- limite de avaliações
    limite_comparacao   INT             NOT NULL DEFAULT 0,   -- limite de comparações de perfis
    status_plano        ENUM('ATIVO', 'INATIVO')      NOT NULL DEFAULT 'ATIVO',
    data_ativacao       DATE            NULL,
    data_expiracao      DATE            NULL
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: EMPRESA
-- Apenas empresas se cadastram e acessam o sistema
-- ============================================================================
CREATE TABLE empresa (
    cnpj                CHAR(14)        PRIMARY KEY,
    nome_empresa        VARCHAR(120)    NOT NULL,
    email                VARCHAR(150)   NOT NULL UNIQUE,
    senha                VARCHAR(255)   NOT NULL,           -- hash (BCrypt)
    pais                 VARCHAR(60)    NULL,
    estado               VARCHAR(60)    NULL,
    cidade               VARCHAR(60)    NULL,
    bairro               VARCHAR(60)    NULL,
    endereco             VARCHAR(150)   NULL,
    telefone             VARCHAR(20)    NULL,
    foto_url             VARCHAR(255)   NULL,
    data_cadastro         DATE          NOT NULL DEFAULT (CURRENT_DATE),
    fk_plano_id_plano    INT            NOT NULL,
    CONSTRAINT fk_empresa_plano
        FOREIGN KEY (fk_plano_id_plano) REFERENCES plano_de_assinatura(id_plano)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: CANDIDATO
-- Dados técnicos públicos obtidos da API do GitHub (nunca alterados manualmente)
-- ============================================================================
CREATE TABLE candidato (
    node_id              BIGINT         PRIMARY KEY,        -- nodeID do GitHub
    nome_candidato        VARCHAR(150)  NULL,
    username              VARCHAR(60)   NOT NULL UNIQUE,
    localizacao           VARCHAR(120)  NULL,
    num_repositorios      INT           NOT NULL DEFAULT 0,
    bio                   TEXT          NULL,
    avatar_url             VARCHAR(255) NULL,
    linguagem_principal    VARCHAR(60)  NULL,
    data_ultima_sincronizacao DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,
    email                 VARCHAR(150)  NULL UNIQUE,          -- preenchido apenas se o candidato se autocadastrar
    senha                 VARCHAR(255)  NULL                  -- hash (BCrypt); nulo para candidatos só sincronizados via busca
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: REPOSITORIO
-- Repositórios públicos do candidato
-- ============================================================================
CREATE TABLE repositorio (
    url_repositorio       VARCHAR(255)  PRIMARY KEY,
    nome_repositorio       VARCHAR(150) NOT NULL,
    descricao               TEXT        NULL,
    ultimo_commit           DATETIME    NULL,
    linguagem_principal     VARCHAR(60) NULL,
    branch_padrao           VARCHAR(60) NULL DEFAULT 'main',
    numero_issue            INT         NOT NULL DEFAULT 0,
    numero_fork             INT         NOT NULL DEFAULT 0,
    numero_estrela          INT         NOT NULL DEFAULT 0,
    fk_candidato_node_id    BIGINT      NOT NULL,
    CONSTRAINT fk_repositorio_candidato
        FOREIGN KEY (fk_candidato_node_id) REFERENCES candidato(node_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: BUSCA
-- Registro das buscas realizadas (filtros e termos)
-- ============================================================================
CREATE TABLE busca (
    id_busca             BIGINT AUTO_INCREMENT PRIMARY KEY,
    filtro_localizacao    VARCHAR(120) NULL,
    filtro_linguagem       VARCHAR(60) NULL,
    termo_pesquisado        VARCHAR(150) NULL,
    data_busca              DATE       NOT NULL DEFAULT (CURRENT_DATE),
    hora_busca               TIME      NOT NULL DEFAULT (CURRENT_TIME)
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: EMPRESA_BUSCA (associativa N:N)
-- Relaciona empresas às buscas que realizaram
-- ============================================================================
CREATE TABLE empresa_busca (
    fk_busca_id_busca    BIGINT       NOT NULL,
    fk_empresa_cnpj       CHAR(14)    NOT NULL,
    PRIMARY KEY (fk_busca_id_busca, fk_empresa_cnpj),
    CONSTRAINT fk_empresabusca_busca
        FOREIGN KEY (fk_busca_id_busca) REFERENCES busca(id_busca)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_empresabusca_empresa
        FOREIGN KEY (fk_empresa_cnpj) REFERENCES empresa(cnpj)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: EMPRESA_CANDIDATO (associativa N:N)
-- Favoritos e avaliações privadas de cada empresa sobre um candidato
-- ============================================================================
CREATE TABLE empresa_candidato (
    fk_empresa_cnpj        CHAR(14)   NOT NULL,
    fk_candidato_node_id    BIGINT    NOT NULL,
    favorito                 BOOLEAN  NOT NULL DEFAULT FALSE,
    comentario                TEXT    NULL,
    privada                    BOOLEAN NOT NULL DEFAULT TRUE,
    data_avaliacao               DATETIME NULL,
    PRIMARY KEY (fk_empresa_cnpj, fk_candidato_node_id),
    CONSTRAINT fk_empresacandidato_empresa
        FOREIGN KEY (fk_empresa_cnpj) REFERENCES empresa(cnpj)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_empresacandidato_candidato
        FOREIGN KEY (fk_candidato_node_id) REFERENCES candidato(node_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- TABELA: SOLICITACAO_SUPORTE
-- Mensagens de contato enviadas por empresas ou candidatos autenticados
-- ============================================================================
CREATE TABLE solicitacao_suporte (
    id_solicitacao        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_solicitante        VARCHAR(20)  NOT NULL,             -- EMPRESA ou CANDIDATO
    nome_solicitante        VARCHAR(150) NOT NULL,
    email_solicitante       VARCHAR(150) NOT NULL,
    assunto                  VARCHAR(150) NOT NULL,
    mensagem                  TEXT        NOT NULL,
    data_criacao                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================================
-- ÍNDICES de apoio para buscas e filtros
-- ============================================================================
CREATE INDEX idx_candidato_localizacao ON candidato(localizacao);
CREATE INDEX idx_candidato_linguagem ON candidato(linguagem_principal);
CREATE INDEX idx_repositorio_linguagem ON repositorio(linguagem_principal);
CREATE INDEX idx_busca_termo ON busca(termo_pesquisado);
CREATE INDEX idx_empresa_email ON empresa(email);
CREATE INDEX idx_candidato_email ON candidato(email);
