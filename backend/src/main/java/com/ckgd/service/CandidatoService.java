package com.ckgd.service;

import com.ckgd.dto.CandidatoResponse;
import com.ckgd.dto.RepositorioResponse;
import com.ckgd.dto.github.GitHubDtos.*;
import com.ckgd.entity.Candidato;
import com.ckgd.entity.EmpresaCandidato;
import com.ckgd.entity.Repositorio;
import com.ckgd.exception.ResourceNotFoundException;
import com.ckgd.repository.CandidatoRepository;
import com.ckgd.repository.EmpresaCandidatoRepository;
import com.ckgd.repository.RepositorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final RepositorioRepository repositorioRepository;
    private final EmpresaCandidatoRepository empresaCandidatoRepository;
    private final GitHubService gitHubService;

    public CandidatoService(CandidatoRepository candidatoRepository,
                             RepositorioRepository repositorioRepository,
                             EmpresaCandidatoRepository empresaCandidatoRepository,
                             GitHubService gitHubService) {
        this.candidatoRepository = candidatoRepository;
        this.repositorioRepository = repositorioRepository;
        this.empresaCandidatoRepository = empresaCandidatoRepository;
        this.gitHubService = gitHubService;
    }

    /**
     * Busca candidatos no GitHub, persiste/atualiza os dados técnicos localmente
     * (cache) e retorna a lista já pronta para o frontend.
     */
    @Transactional
    public List<CandidatoResponse> buscarESincronizar(String termo, String linguagem, String localizacao, String cnpjEmpresa) {
        List<UserSummary> usuarios = gitHubService.buscarUsuarios(termo, linguagem, localizacao, 12);
        List<CandidatoResponse> resultado = new ArrayList<>();

        for (UserSummary usuario : usuarios) {
            try {
                UserDetail detalhe = gitHubService.buscarDetalhesUsuario(usuario.login);
                List<RepoSummary> repos = gitHubService.buscarRepositorios(usuario.login, 6);

                Candidato candidato = sincronizarCandidato(usuario, detalhe, repos);
                resultado.add(paraResponse(candidato, cnpjEmpresa));
            } catch (Exception e) {
                // Se um usuário específico falhar (ex.: rate limit), pulamos e seguimos com os demais
            }
        }

        return resultado;
    }

    /**
     * Busca o perfil de um usuário específico no GitHub pelo username e sincroniza
     * localmente (mesma lógica usada na busca da empresa). Usado no autocadastro do candidato.
     */
    @Transactional
    public Candidato sincronizarPorUsername(String usernameGithub) {
        UserDetail detalhe = gitHubService.buscarDetalhesUsuario(usernameGithub);
        if (detalhe == null || detalhe.id == null) {
            throw new ResourceNotFoundException("Usuário do GitHub não encontrado: " + usernameGithub);
        }

        UserSummary resumo = new UserSummary();
        resumo.id = detalhe.id;
        resumo.login = detalhe.login;
        resumo.avatarUrl = detalhe.avatarUrl;

        List<RepoSummary> repos = gitHubService.buscarRepositorios(usernameGithub, 6);
        return sincronizarCandidato(resumo, detalhe, repos);
    }

    private Candidato sincronizarCandidato(UserSummary usuario, UserDetail detalhe, List<RepoSummary> repos) {
        Long nodeId = usuario.id; // usamos o id numérico do GitHub como node_id (BIGINT)

        Candidato candidato = candidatoRepository.findById(nodeId).orElse(new Candidato());
        candidato.setNodeId(nodeId);
        candidato.setUsername(usuario.login);
        candidato.setAvatarUrl(usuario.avatarUrl);

        if (detalhe != null) {
            candidato.setNomeCandidato(detalhe.name != null ? detalhe.name : usuario.login);
            candidato.setLocalizacao(detalhe.location);
            candidato.setBio(detalhe.bio);
            candidato.setNumRepositorios(detalhe.publicRepos != null ? detalhe.publicRepos : 0);
        }

        String linguagemMaisUsada = repos.stream()
                .map(r -> r.language)
                .filter(l -> l != null && !l.isBlank())
                .findFirst()
                .orElse(candidato.getLinguagemPrincipal());
        candidato.setLinguagemPrincipal(linguagemMaisUsada);
        candidato.setDataUltimaSincronizacao(LocalDateTime.now());

        candidato = candidatoRepository.save(candidato);

        for (RepoSummary repo : repos) {
            if (repo.fork != null && repo.fork) continue; // ignora forks para focar em trabalho autoral

            Repositorio entidade = repositorioRepository.findById(repo.htmlUrl).orElse(new Repositorio());
            entidade.setUrlRepositorio(repo.htmlUrl);
            entidade.setNomeRepositorio(repo.name);
            entidade.setDescricao(repo.description);
            entidade.setLinguagemPrincipal(repo.language);
            entidade.setBranchPadrao(repo.defaultBranch);
            entidade.setNumeroIssue(repo.openIssuesCount != null ? repo.openIssuesCount : 0);
            entidade.setNumeroFork(repo.forksCount != null ? repo.forksCount : 0);
            entidade.setNumeroEstrela(repo.stargazersCount != null ? repo.stargazersCount : 0);
            entidade.setCandidato(candidato);
            if (repo.pushedAt != null) {
                entidade.setUltimoCommit(OffsetDateTime.parse(repo.pushedAt).toLocalDateTime());
            }
            repositorioRepository.save(entidade);
        }

        return candidato;
    }

    public CandidatoResponse buscarPerfilCompleto(Long nodeId, String cnpjEmpresa) {
        Candidato candidato = candidatoRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidato não encontrado"));
        return paraResponse(candidato, cnpjEmpresa);
    }

    private CandidatoResponse paraResponse(Candidato candidato, String cnpjEmpresa) {
        CandidatoResponse dto = new CandidatoResponse();
        dto.setNodeId(candidato.getNodeId());
        dto.setNomeCandidato(candidato.getNomeCandidato());
        dto.setUsername(candidato.getUsername());
        dto.setLocalizacao(candidato.getLocalizacao());
        dto.setNumRepositorios(candidato.getNumRepositorios());
        dto.setBio(candidato.getBio());
        dto.setAvatarUrl(candidato.getAvatarUrl());
        dto.setLinguagemPrincipal(candidato.getLinguagemPrincipal());

        List<Repositorio> repos = repositorioRepository.findByCandidato_NodeId(candidato.getNodeId());
        List<RepositorioResponse> reposResponse = new ArrayList<>();
        int totalEstrelas = 0;
        for (Repositorio r : repos) {
            RepositorioResponse rr = new RepositorioResponse();
            rr.setUrlRepositorio(r.getUrlRepositorio());
            rr.setNomeRepositorio(r.getNomeRepositorio());
            rr.setDescricao(r.getDescricao());
            rr.setLinguagemPrincipal(r.getLinguagemPrincipal());
            rr.setBranchPadrao(r.getBranchPadrao());
            rr.setNumeroIssue(r.getNumeroIssue());
            rr.setNumeroFork(r.getNumeroFork());
            rr.setNumeroEstrela(r.getNumeroEstrela());
            reposResponse.add(rr);
            totalEstrelas += r.getNumeroEstrela() != null ? r.getNumeroEstrela() : 0;
        }
        dto.setRepositorios(reposResponse);
        dto.setTotalEstrelas(totalEstrelas);

        if (cnpjEmpresa != null) {
            Optional<EmpresaCandidato> vinculo = empresaCandidatoRepository
                    .findByEmpresa_CnpjAndCandidato_NodeId(cnpjEmpresa, candidato.getNodeId());
            dto.setFavorito(vinculo.map(EmpresaCandidato::getFavorito).orElse(false));
        }

        return dto;
    }
}
