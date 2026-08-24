package com.ckgd.service;

import com.ckgd.dto.github.GitHubDtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubService {

    private final RestClient githubRestClient;

    public GitHubService(RestClient githubRestClient) {
        this.githubRestClient = githubRestClient;
    }

    /**
     * Busca usuários no GitHub combinando termo livre, linguagem e localização,
     * seguindo a sintaxe de busca avançada da API do GitHub.
     */
    public List<UserSummary> buscarUsuarios(String termo, String linguagem, String localizacao, int quantidade) {
        StringBuilder query = new StringBuilder();

        if (termo != null && !termo.isBlank()) {
            query.append(termo.trim());
        } else {
            query.append("developer"); // termo neutro, sempre precisamos de algo antes dos qualifiers
        }
        query.append(" type:user");

        if (linguagem != null && !linguagem.isBlank()) {
            query.append(" language:").append(linguagem.trim());
        }
        if (localizacao != null && !localizacao.isBlank()) {
            query.append(" location:").append(localizacao.trim());
        }

        SearchUsersResponse response = githubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/users")
                        .queryParam("q", query.toString())
                        .queryParam("per_page", Math.min(quantidade, 30))
                        .build())
                .retrieve()
                .body(SearchUsersResponse.class);

        if (response == null || response.items == null) {
            return new ArrayList<>();
        }
        return response.items;
    }

    public UserDetail buscarDetalhesUsuario(String username) {
        return githubRestClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(UserDetail.class);
    }

    public List<RepoSummary> buscarRepositorios(String username, int quantidade) {
        List<RepoSummary> repos = githubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("sort", "updated")
                        .queryParam("per_page", quantidade)
                        .build(username))
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<RepoSummary>>() {});

        return repos == null ? new ArrayList<>() : repos;
    }
}
