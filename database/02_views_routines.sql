-- ============================================================================
-- CKGD - Views e Rotinas Armazenadas
-- ============================================================================
USE ckgd;

-- ----------------------------------------------------------------------------
-- VIEW: vw_favoritos_empresa
-- Consolida os candidatos favoritados por empresa, já com dados do candidato
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- VIEW: vw_uso_plano
-- Mostra o consumo de cada empresa frente aos limites do seu plano
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- VIEW: vw_ranking_linguagens
-- Ranking das linguagens mais buscadas pelas empresas (uso estratégico)
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_ranking_linguagens AS
SELECT
    filtro_linguagem AS linguagem,
    COUNT(*)         AS total_buscas
FROM busca
WHERE filtro_linguagem IS NOT NULL
GROUP BY filtro_linguagem
ORDER BY total_buscas DESC;

-- ----------------------------------------------------------------------------
-- VIEW: vw_candidato_resumo
-- Perfil resumido do candidato com estatísticas agregadas dos repositórios
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- ROTINA: sp_verificar_limite_busca
-- Verifica se a empresa ainda pode realizar buscas dentro do limite do plano
-- ----------------------------------------------------------------------------
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
        SET p_pode_buscar = TRUE; -- 0 = ilimitado, convenção do plano
    ELSE
        SET p_pode_buscar = (v_usadas < v_limite);
    END IF;
END$$
DELIMITER ;

-- ----------------------------------------------------------------------------
-- ROTINA: sp_registrar_busca
-- Registra uma nova busca e o vínculo com a empresa que a realizou
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- FUNCTION: fn_contar_favoritos
-- Retorna quantos candidatos uma empresa já favoritou
-- ----------------------------------------------------------------------------
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
