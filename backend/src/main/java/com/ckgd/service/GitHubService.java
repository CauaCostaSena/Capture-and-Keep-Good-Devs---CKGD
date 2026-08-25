package com.ckgd.service;

import com.ckgd.dto.github.GitHubDtos.*;
import com.ckgd.exception.BusinessException;
import com.ckgd.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class GitHubService {

    private final RestClient githubRestClient;

    public GitHubService(RestClient githubRestClient) {
        this.githubRestClient = githubRestClient;
    }

    /** Traduz erros da API do GitHub (rate limit, usuário inexistente, etc.) em mensagens claras. */
    private <T> T chamar(Supplier<T> chamada) {
        try {
            return chamada.get();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 403 && e.getResponseBodyAsString().toLowerCase().contains("rate limit")) {
                throw new BusinessException("Limite de requisições à API do GitHub excedido. Tente novamente em alguns minutos "
                        + "ou configure a variável de ambiente CKGD_GITHUB_TOKEN com um token do GitHub para aumentar o limite.");
            }
            if (e.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("Usuário ou recurso não encontrado no GitHub");
            }
            throw new BusinessException("Erro ao consultar a API do GitHub (status " + e.getStatusCode().value() + ")");
        }
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

        SearchUsersResponse response = chamar(() -> githubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/users")
                        .queryParam("q", query.toString())
                        .queryParam("per_page", Math.min(quantidade, 30))
                        .build())
                .retrieve()
                .body(SearchUsersResponse.class));

        if (response == null || response.items == null) {
            return new ArrayList<>();
        }
        return response.items;
    }

    public UserDetail buscarDetalhesUsuario(String username) {
        return chamar(() -> githubRestClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(UserDetail.class));
    }

    public List<RepoSummary> buscarRepositorios(String username, int quantidade) {
        List<RepoSummary> repos = chamar(() -> githubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("sort", "updated")
                        .queryParam("per_page", quantidade)
                        .build(username))
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<RepoSummary>>() {}));

        return repos == null ? new ArrayList<>() : repos;
    }
}
