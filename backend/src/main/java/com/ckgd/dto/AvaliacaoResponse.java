package com.ckgd.dto;

import com.ckgd.entity.EmpresaCandidato;
import java.time.LocalDateTime;

public class AvaliacaoResponse {
    private Long nodeIdCandidato;
    private String nomeCandidato;
    private String username;
    private Boolean favorito;
    private String comentario;
    private Boolean privada;
    private LocalDateTime dataAvaliacao;

    public static AvaliacaoResponse from(EmpresaCandidato ec) {
        AvaliacaoResponse dto = new AvaliacaoResponse();
        dto.nodeIdCandidato = ec.getCandidato().getNodeId();
        dto.nomeCandidato = ec.getCandidato().getNomeCandidato();
        dto.username = ec.getCandidato().getUsername();
        dto.favorito = ec.getFavorito();
        dto.comentario = ec.getComentario();
        dto.privada = ec.getPrivada();
        dto.dataAvaliacao = ec.getDataAvaliacao();
        return dto;
    }

    public Long getNodeIdCandidato() { return nodeIdCandidato; }
    public String getNomeCandidato() { return nomeCandidato; }
    public String getUsername() { return username; }
    public Boolean getFavorito() { return favorito; }
    public String getComentario() { return comentario; }
    public Boolean getPrivada() { return privada; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
}
