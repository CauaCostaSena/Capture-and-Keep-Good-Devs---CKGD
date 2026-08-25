-- ============================================================================
-- CKGD - SETUP COMPLETO DO BANCO DE DADOS
-- ============================================================================
-- Este arquivo junta, na ordem correta, os 3 scripts do projeto:
--   01_schema.sql           -> cria o banco e as tabelas
--   02_views_routines.sql   -> cria as views, procedures e função
--   03_data_manipulation.sql-> insere os dados de exemplo
--
-- COMO USAR NO MYSQL WORKBENCH:
--   1. Abra o MySQL Workbench e conecte na instância local (localhost:3306)
--   2. File > Open SQL Script...  e selecione este arquivo
--   3. Clique no raio (Execute All, ou Ctrl+Shift+Enter)
--   4. Clique com o botão direito em SCHEMAS > Refresh All para ver o "ckgd"
--
-- ATENÇÃO: a primeira linha APAGA o banco "ckgd" se ele já existir.
-- ============================================================================


-- ############################################################################
-- PARTE 1 - SCHEMA (tabelas e índices)
-- ############################################################################

DROP DATABASE IF EXISTS ckgd;
CREATE DATABASE ckgd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ckgd;

-- TABELA: PLANO_DE_ASSINATURA -----------------------------------------------
CREATE TABLE plano_de_assinatura (
    id_plano            INT AUTO_INCREMENT PRIMARY KEY,
    nome_plano          VARCHAR(60)     NOT NULL,
    preco_plano         DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    periodicidade       ENUM('MENSAL', 'ANUAL')       NOT NULL DEFAULT 'MENSAL',
    limite_requisicao   INT             NOT NULL DEFAULT 0,
    limite_avaliacao    INT             NOT NULL DEFAULT 0,
    limite_comparacao   INT             NOT NULL DEFAULT 0,
    status_plano        ENUM('ATIVO', 'INATIVO')      NOT NULL DEFAULT 'ATIVO',
    data_ativacao       DATE            NULL,
    data_expiracao      DATE            NULL
) ENGINE=InnoDB;

-- TABELA: EMPRESA ------------------------------------------------------------
CREATE TABLE empresa (
    cnpj                CHAR(14)        PRIMARY KEY,
    nome_empresa        VARCHAR(120)    NOT NULL,
    email                VARCHAR(150)   NOT NULL UNIQUE,
    senha                VARCHAR(255)   NOT NULL,
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

-- TABELA: CANDIDATO ----------------------------------------------------------
CREATE TABLE candidato (
    node_id              BIGINT         PRIMARY KEY,
    nome_candidato        VARCHAR(150)  NULL,
    username              VARCHAR(60)   NOT NULL UNIQUE,
    localizacao           VARCHAR(120)  NULL,
    num_repositorios      INT           NOT NULL DEFAULT 0,
    bio                   TEXT          NULL,
    avatar_url             VARCHAR(255) NULL,
    linguagem_principal    VARCHAR(60)  NULL,
    data_ultima_sincronizacao DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,
    email                 VARCHAR(150)  NULL UNIQUE,
    senha                 VARCHAR(255)  NULL
) ENGINE=InnoDB;

-- TABELA: REPOSITORIO --------------------------------------------------------
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

-- TABELA: BUSCA --------------------------------------------------------------
CREATE TABLE busca (
    id_busca             BIGINT AUTO_INCREMENT PRIMARY KEY,
    filtro_localizacao    VARCHAR(120) NULL,
    filtro_linguagem       VARCHAR(60) NULL,
    termo_pesquisado        VARCHAR(150) NULL,
    data_busca              DATE       NOT NULL DEFAULT (CURRENT_DATE),
    hora_busca               TIME      NOT NULL DEFAULT (CURRENT_TIME)
) ENGINE=InnoDB;

-- TABELA: EMPRESA_BUSCA (N:N) ------------------------------------------------
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

-- TABELA: EMPRESA_CANDIDATO (N:N) --------------------------------------------
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

-- TABELA: SOLICITACAO_SUPORTE -------------------------------------------------
CREATE TABLE solicitacao_suporte (
    id_solicitacao        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_solicitante        VARCHAR(20)  NOT NULL,
    nome_solicitante        VARCHAR(150) NOT NULL,
    email_solicitante       VARCHAR(150) NOT NULL,
    assunto                  VARCHAR(150) NOT NULL,
    mensagem                  TEXT        NOT NULL,
    data_criacao                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ÍNDICES de apoio -----------------------------------------------------------
CREATE INDEX idx_candidato_localizacao ON candidato(localizacao);
CREATE INDEX idx_candidato_linguagem ON candidato(linguagem_principal);
CREATE INDEX idx_repositorio_linguagem ON repositorio(linguagem_principal);
CREATE INDEX idx_busca_termo ON busca(termo_pesquisado);
CREATE INDEX idx_empresa_email ON empresa(email);
CREATE INDEX idx_candidato_email ON candidato(email);


-- ############################################################################
-- PARTE 2 - VIEWS E ROTINAS
-- ############################################################################

CREATE OR REPLACE VIEW vw_favoritos_empresa AS
SELECT
    ec.fk_empresa_cnpj      AS cnpj_empresa,
    e.nome_empresa,
    c.node_id,
    c.nome_candidato,
    c.username,
    c.localizacao,
    c.linguagem_principal,
    ec.comentario,
    ec.privada,
    ec.data_avaliacao
FROM empresa_candidato ec
JOIN empresa e   ON e.cnpj = ec.fk_empresa_cnpj
JOIN candidato c ON c.node_id = ec.fk_candidato_node_id
WHERE ec.favorito = TRUE;

CREATE OR REPLACE VIEW vw_uso_plano AS
SELECT
    e.cnpj,
    e.nome_empresa,
    p.nome_plano,
    p.limite_requisicao,
    (SELECT COUNT(*) FROM empresa_busca eb WHERE eb.fk_empresa_cnpj = e.cnpj) AS buscas_realizadas,
    p.limite_avaliacao,
    (SELECT COUNT(*) FROM empresa_candidato ec
        WHERE ec.fk_empresa_cnpj = e.cnpj AND ec.comentario IS NOT NULL) AS avaliacoes_realizadas,
    p.status_plano,
    p.data_expiracao
FROM empresa e
JOIN plano_de_assinatura p ON p.id_plano = e.fk_plano_id_plano;

CREATE OR REPLACE VIEW vw_ranking_linguagens AS
SELECT
    filtro_linguagem AS linguagem,
    COUNT(*)         AS total_buscas
FROM busca
WHERE filtro_linguagem IS NOT NULL
GROUP BY filtro_linguagem
ORDER BY total_buscas DESC;

CREATE OR REPLACE VIEW vw_candidato_resumo AS
SELECT
    c.node_id,
    c.nome_candidato,
    c.username,
    c.localizacao,
    c.bio,
    c.linguagem_principal,
    COUNT(r.url_repositorio)         AS total_repositorios,
    COALESCE(SUM(r.numero_estrela),0) AS total_estrelas,
    COALESCE(SUM(r.numero_fork),0)    AS total_forks,
    COUNT(DISTINCT r.linguagem_principal) AS total_linguagens
FROM candidato c
LEFT JOIN repositorio r ON r.fk_candidato_node_id = c.node_id
GROUP BY c.node_id, c.nome_candidato, c.username, c.localizacao, c.bio, c.linguagem_principal;

-- PROCEDURE: sp_verificar_limite_busca ---------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_verificar_limite_busca(
    IN p_cnpj CHAR(14),
    OUT p_pode_buscar BOOLEAN
)
BEGIN
    DECLARE v_limite INT;
    DECLARE v_usadas INT;

    SELECT p.limite_requisicao INTO v_limite
    FROM empresa e JOIN plano_de_assinatura p ON p.id_plano = e.fk_plano_id_plano
    WHERE e.cnpj = p_cnpj;

    SELECT COUNT(*) INTO v_usadas
    FROM empresa_busca
    WHERE fk_empresa_cnpj = p_cnpj;

    IF v_limite = 0 THEN
        SET p_pode_buscar = TRUE; -- 0 = ilimitado
    ELSE
        SET p_pode_buscar = (v_usadas < v_limite);
    END IF;
END$$
DELIMITER ;

-- PROCEDURE: sp_registrar_busca ----------------------------------------------
DELIMITER $$
CREATE PROCEDURE sp_registrar_busca(
    IN p_cnpj CHAR(14),
    IN p_termo VARCHAR(150),
    IN p_linguagem VARCHAR(60),
    IN p_localizacao VARCHAR(120)
)
BEGIN
    DECLARE v_id_busca BIGINT;

    INSERT INTO busca (filtro_localizacao, filtro_linguagem, termo_pesquisado)
    VALUES (p_localizacao, p_linguagem, p_termo);

    SET v_id_busca = LAST_INSERT_ID();

    INSERT INTO empresa_busca (fk_busca_id_busca, fk_empresa_cnpj)
    VALUES (v_id_busca, p_cnpj);
END$$
DELIMITER ;

-- FUNCTION: fn_contar_favoritos ----------------------------------------------
DELIMITER $$
CREATE FUNCTION fn_contar_favoritos(p_cnpj CHAR(14))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_total INT;
    SELECT COUNT(*) INTO v_total
    FROM empresa_candidato
    WHERE fk_empresa_cnpj = p_cnpj AND favorito = TRUE;
    RETURN v_total;
END$$
DELIMITER ;


-- ############################################################################
-- PARTE 3 - DADOS DE EXEMPLO
-- ############################################################################

INSERT INTO plano_de_assinatura (nome_plano, preco_plano, periodicidade, limite_requisicao, limite_avaliacao, limite_comparacao, status_plano, data_ativacao, data_expiracao) VALUES
('Free',       0.00,   'MENSAL', 10,  5,  3, 'ATIVO', CURDATE(), NULL),
('Pro',      199.90,   'MENSAL', 200, 100, 50, 'ATIVO', CURDATE(), NULL),
('Enterprise', 0.00,   'ANUAL',  0,   0,   0, 'ATIVO', CURDATE(), NULL);

INSERT INTO empresa (cnpj, nome_empresa, email, senha, pais, estado, cidade, bairro, endereco, fk_plano_id_plano) VALUES
('11222333000181', 'Intel. Inc', 'contato@intel.com', '$2a$10$abcdefghijklmnopqrstuv', 'EUA', 'Califórnia', 'Santa Clara', 'Centro', 'Av. Central, 100', 2),
('22333444000192', 'TechNova Ltda', 'rh@technova.com.br', '$2a$10$abcdefghijklmnopqrstuv', 'Brasil', 'São Paulo', 'São Paulo', 'Pinheiros', 'Rua das Flores, 55', 1);

INSERT INTO candidato (node_id, nome_candidato, username, localizacao, num_repositorios, bio, linguagem_principal) VALUES
(1001, 'Bernardo Costa Lima', 'bclima-dev', 'Brasil', 42, 'Backend developer, Java & C++', 'Java'),
(1002, 'Aldair Sampaio', 'aldairsampaio', 'Brasil', 18, 'Back-end Java developer', 'Java'),
(1003, 'João Victor Nascimento', 'jvnasc', 'Brasil', 27, 'Fullstack developer', 'Java');

INSERT INTO repositorio (url_repositorio, nome_repositorio, descricao, ultimo_commit, linguagem_principal, branch_padrao, numero_issue, numero_fork, numero_estrela, fk_candidato_node_id) VALUES
('https://github.com/bclima-dev/api-rest', 'api-rest', 'API REST em Spring Boot', NOW(), 'Java', 'main', 3, 12, 145, 1001),
('https://github.com/bclima-dev/portfolio', 'portfolio', 'Portfólio pessoal', NOW(), 'HTML', 'main', 0, 2, 8, 1001),
('https://github.com/aldairsampaio/ecommerce-back', 'ecommerce-back', 'Backend de e-commerce', NOW(), 'Java', 'main', 5, 20, 210, 1002);

CALL sp_registrar_busca('11222333000181', 'Desenvolvedor Fullstack', 'Java', 'Brasil');

INSERT INTO empresa_candidato (fk_empresa_cnpj, fk_candidato_node_id, favorito, comentario, privada, data_avaliacao)
VALUES ('11222333000181', 1001, TRUE, 'Excelente domínio de Java e boas práticas de código.', TRUE, NOW());


-- ############################################################################
-- PARTE 4 - CONSULTAS DE VALIDAÇÃO (conferem se deu tudo certo)
-- ############################################################################

SHOW TABLES;

CALL sp_verificar_limite_busca('11222333000181', @pode_buscar);
SELECT @pode_buscar AS pode_realizar_busca;

SELECT * FROM vw_favoritos_empresa WHERE cnpj_empresa = '11222333000181';
SELECT * FROM vw_uso_plano;
SELECT * FROM vw_candidato_resumo WHERE node_id = 1001;
SELECT * FROM vw_ranking_linguagens;
SELECT fn_contar_favoritos('11222333000181') AS total_favoritos;
