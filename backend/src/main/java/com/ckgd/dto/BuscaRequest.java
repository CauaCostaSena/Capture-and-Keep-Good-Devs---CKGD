package com.ckgd.dto;

public class BuscaRequest {
    private String termo;
    private String linguagem;
    private String localizacao;

    public String getTermo() { return termo; }
    public void setTermo(String termo) { this.termo = termo; }

    public String getLinguagem() { return linguagem; }
    public void setLinguagem(String linguagem) { this.linguagem = linguagem; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
}
