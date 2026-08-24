package com.ckgd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repositorio")
public class Repositorio {

    @Id
    @Column(name = "url_repositorio")
    private String urlRepositorio;

    @Column(name = "nome_repositorio", nullable = false, length = 150)
    private String nomeRepositorio;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ultimo_commit")
    private LocalDateTime ultimoCommit;

    @Column(name = "linguagem_principal", length = 60)
    private String linguagemPrincipal;

    @Column(name = "branch_padrao", length = 60)
    private String branchPadrao = "main";

    @Column(name = "numero_issue", nullable = false)
    private Integer numeroIssue = 0;

    @Column(name = "numero_fork", nullable = false)
    private Integer numeroFork = 0;

    @Column(name = "numero_estrela", nullable = false)
    private Integer numeroEstrela = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_candidato_node_id", nullable = false)
    @JsonIgnore
    private Candidato candidato;

    public Repositorio() {}

    // Getters e Setters
    public String getUrlRepositorio() { return urlRepositorio; }
    public void setUrlRepositorio(String urlRepositorio) { this.urlRepositorio = urlRepositorio; }

    public String getNomeRepositorio() { return nomeRepositorio; }
    public void setNomeRepositorio(String nomeRepositorio) { this.nomeRepositorio = nomeRepositorio; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getUltimoCommit() { return ultimoCommit; }
    public void setUltimoCommit(LocalDateTime ultimoCommit) { this.ultimoCommit = ultimoCommit; }

    public String getLinguagemPrincipal() { return linguagemPrincipal; }
    public void setLinguagemPrincipal(String linguagemPrincipal) { this.linguagemPrincipal = linguagemPrincipal; }

    public String getBranchPadrao() { return branchPadrao; }
    public void setBranchPadrao(String branchPadrao) { this.branchPadrao = branchPadrao; }

    public Integer getNumeroIssue() { return numeroIssue; }
    public void setNumeroIssue(Integer numeroIssue) { this.numeroIssue = numeroIssue; }

    public Integer getNumeroFork() { return numeroFork; }
    public void setNumeroFork(Integer numeroFork) { this.numeroFork = numeroFork; }

    public Integer getNumeroEstrela() { return numeroEstrela; }
    public void setNumeroEstrela(Integer numeroEstrela) { this.numeroEstrela = numeroEstrela; }

    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }
}
