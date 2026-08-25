package com.ckgd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidato")
public class Candidato {

    @Id
    @Column(name = "node_id")
    private Long nodeId;

    @Column(name = "nome_candidato", length = 150)
    private String nomeCandidato;

    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "localizacao", length = 120)
    private String localizacao;

    @Column(name = "num_repositorios", nullable = false)
    private Integer numRepositorios = 0;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "linguagem_principal", length = 60)
    private String linguagemPrincipal;

    @Column(name = "data_ultima_sincronizacao")
    private LocalDateTime dataUltimaSincronizacao = LocalDateTime.now();

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "senha")
    @JsonIgnore
    private String senha; // hash BCrypt; nulo para candidatos que só existem via sincronização (busca da empresa)

    @OneToMany(mappedBy = "candidato", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Repositorio> repositorios = new ArrayList<>();

    public Candidato() {}

    // Getters e Setters
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    public String getNomeCandidato() { return nomeCandidato; }
    public void setNomeCandidato(String nomeCandidato) { this.nomeCandidato = nomeCandidato; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public Integer getNumRepositorios() { return numRepositorios; }
    public void setNumRepositorios(Integer numRepositorios) { this.numRepositorios = numRepositorios; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getLinguagemPrincipal() { return linguagemPrincipal; }
    public void setLinguagemPrincipal(String linguagemPrincipal) { this.linguagemPrincipal = linguagemPrincipal; }

    public LocalDateTime getDataUltimaSincronizacao() { return dataUltimaSincronizacao; }
    public void setDataUltimaSincronizacao(LocalDateTime d) { this.dataUltimaSincronizacao = d; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public List<Repositorio> getRepositorios() { return repositorios; }
    public void setRepositorios(List<Repositorio> repositorios) { this.repositorios = repositorios; }
}
