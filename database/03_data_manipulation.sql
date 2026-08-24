-- ============================================================================
-- CKGD - Script de Manipulação e Consulta
-- Dados de exemplo para simular o uso real da plataforma
-- ============================================================================
USE ckgd;

-- ----------------------------------------------------------------------------
-- INSERÇÃO: Planos de assinatura
-- ----------------------------------------------------------------------------
INSERT INTO plano_de_assinatura (nome_plano, preco_plano, periodicidade, limite_requisicao, limite_avaliacao, limite_comparacao, status_plano, data_ativacao, data_expiracao) VALUES
('Free',       0.00,   'MENSAL', 10,  5,  3, 'ATIVO', CURDATE(), NULL),
('Pro',      199.90,   'MENSAL', 200, 100, 50, 'ATIVO', CURDATE(), NULL),
('Enterprise', 0.00,   'ANUAL',  0,   0,   0, 'ATIVO', CURDATE(), NULL); -- 0 = ilimitado

-- ----------------------------------------------------------------------------
-- INSERÇÃO: Empresas
-- ----------------------------------------------------------------------------
INSERT INTO empresa (cnpj, nome_empresa, email, senha, pais, estado, cidade, bairro, endereco, fk_plano_id_plano) VALUES
('11222333000181', 'Intel. Inc', 'contato@intel.com', '$2a$10$abcdefghijklmnopqrstuv', 'EUA', 'Califórnia', 'Santa Clara', 'Centro', 'Av. Central, 100', 2),
('22333444000192', 'TechNova Ltda', 'rh@technova.com.br', '$2a$10$abcdefghijklmnopqrstuv', 'Brasil', 'São Paulo', 'São Paulo', 'Pinheiros', 'Rua das Flores, 55', 1);

-- ----------------------------------------------------------------------------
-- INSERÇÃO: Candidatos (dados que normalmente viriam da API do GitHub)
-- ----------------------------------------------------------------------------
INSERT INTO candidato (node_id, nome_candidato, username, localizacao, num_repositorios, bio, linguagem_principal) VALUES
(1001, 'Bernardo Costa Lima', 'bclima-dev', 'Brasil', 42, 'Backend developer, Java & C++', 'Java'),
(1002, 'Aldair Sampaio', 'aldairsampaio', 'Brasil', 18, 'Back-end Java developer', 'Java'),
(1003, 'João Victor Nascimento', 'jvnasc', 'Brasil', 27, 'Fullstack developer', 'Java');

-- ----------------------------------------------------------------------------
-- INSERÇÃO: Repositórios
-- ----------------------------------------------------------------------------
INSERT INTO repositorio (url_repositorio, nome_repositorio, descricao, ultimo_commit, linguagem_principal, branch_padrao, numero_issue, numero_fork, numero_estrela, fk_candidato_node_id) VALUES
('https://github.com/bclima-dev/api-rest', 'api-rest', 'API REST em Spring Boot', NOW(), 'Java', 'main', 3, 12, 145, 1001),
('https://github.com/bclima-dev/portfolio', 'portfolio', 'Portfólio pessoal', NOW(), 'HTML', 'main', 0, 2, 8, 1001),
('https://github.com/aldairsampaio/ecommerce-back', 'ecommerce-back', 'Backend de e-commerce', NOW(), 'Java', 'main', 5, 20, 210, 1002);

-- ----------------------------------------------------------------------------
-- SIMULAÇÃO: Registro de busca via procedure
-- ----------------------------------------------------------------------------
CALL sp_registrar_busca('11222333000181', 'Desenvolvedor Fullstack', 'Java', 'Brasil');

-- ----------------------------------------------------------------------------
-- SIMULAÇÃO: Favoritar e avaliar um candidato
-- ----------------------------------------------------------------------------
INSERT INTO empresa_candidato (fk_empresa_cnpj, fk_candidato_node_id, favorito, comentario, privada, data_avaliacao)
VALUES ('11222333000181', 1001, TRUE, 'Excelente domínio de Java e boas práticas de código.', TRUE, NOW());

-- ----------------------------------------------------------------------------
-- CONSULTAS DE VALIDAÇÃO
-- ----------------------------------------------------------------------------

-- Verifica limite de busca de uma empresa antes de permitir nova requisição
CALL sp_verificar_limite_busca('11222333000181', @pode_buscar);
SELECT @pode_buscar AS pode_realizar_busca;

-- Lista os favoritos de uma empresa
SELECT * FROM vw_favoritos_empresa WHERE cnpj_empresa = '11222333000181';

-- Mostra o uso do plano de cada empresa
SELECT * FROM vw_uso_plano;

-- Resumo do perfil de um candidato (para a tela de perfil)
SELECT * FROM vw_candidato_resumo WHERE node_id = 1001;

-- Ranking das linguagens mais buscadas
SELECT * FROM vw_ranking_linguagens;

-- Quantidade de favoritos de uma empresa via função
SELECT fn_contar_favoritos('11222333000181') AS total_favoritos;

-- ----------------------------------------------------------------------------
-- TESTE DE INTEGRIDADE REFERENCIAL (ON DELETE CASCADE)
-- Ao remover um candidato, seus repositórios e vínculos de favorito/avaliação
-- são removidos automaticamente, sem deixar registros órfãos.
-- ----------------------------------------------------------------------------
-- DELETE FROM candidato WHERE node_id = 1002;
-- SELECT * FROM repositorio WHERE fk_candidato_node_id = 1002; -- deve retornar vazio

-- ----------------------------------------------------------------------------
-- ATUALIZAÇÃO de exemplo
-- ----------------------------------------------------------------------------
UPDATE empresa_candidato
SET comentario = 'Reavaliado: também tem boa experiência com testes automatizados.'
WHERE fk_empresa_cnpj = '11222333000181' AND fk_candidato_node_id = 1001;

-- ----------------------------------------------------------------------------
-- EXCLUSÃO de exemplo respeitando integridade referencial
-- ----------------------------------------------------------------------------
DELETE FROM empresa_candidato
WHERE fk_empresa_cnpj = '11222333000181' AND fk_candidato_node_id = 1001 AND 1=0; -- guarda de segurança, não executa por padrão
