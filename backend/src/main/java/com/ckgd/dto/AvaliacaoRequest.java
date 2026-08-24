package com.ckgd.dto;

public class AvaliacaoRequest {
    private Boolean favorito;
    private String comentario;
    private Boolean privada = true;

    public Boolean getFavorito() { return favorito; }
    public void setFavorito(Boolean favorito) { this.favorito = favorito; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Boolean getPrivada() { return privada; }
    public void setPrivada(Boolean privada) { this.privada = privada; }
}
