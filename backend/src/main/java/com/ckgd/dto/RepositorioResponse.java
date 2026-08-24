package com.ckgd.dto;

public class RepositorioResponse {
    private String urlRepositorio;
    private String nomeRepositorio;
    private String descricao;
    private String linguagemPrincipal;
    private String branchPadrao;
    private Integer numeroIssue;
    private Integer numeroFork;
    private Integer numeroEstrela;

    public String getUrlRepositorio() { return urlRepositorio; }
    public void setUrlRepositorio(String urlRepositorio) { this.urlRepositorio = urlRepositorio; }

    public String getNomeRepositorio() { return nomeRepositorio; }
    public void setNomeRepositorio(String nomeRepositorio) { this.nomeRepositorio = nomeRepositorio; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

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
}
