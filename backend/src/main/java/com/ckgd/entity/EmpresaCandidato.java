package com.ckgd.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "empresa_candidato")
public class EmpresaCandidato {

    @EmbeddedId
    private EmpresaCandidatoId id = new EmpresaCandidatoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fkEmpresaCnpj")
    @JoinColumn(name = "fk_empresa_cnpj")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fkCandidatoNodeId")
    @JoinColumn(name = "fk_candidato_node_id")
    private Candidato candidato;

    @Column(name = "favorito", nullable = false)
    private Boolean favorito = false;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "privada", nullable = false)
    private Boolean privada = true;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;

    public EmpresaCandidato() {}

    public EmpresaCandidato(Empresa empresa, Candidato candidato) {
        this.empresa = empresa;
        this.candidato = candidato;
        this.id = new EmpresaCandidatoId(empresa.getCnpj(), candidato.getNodeId());
    }

    public EmpresaCandidatoId getId() { return id; }
    public void setId(EmpresaCandidatoId id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }

    public Boolean getFavorito() { return favorito; }
    public void setFavorito(Boolean favorito) { this.favorito = favorito; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Boolean getPrivada() { return privada; }
    public void setPrivada(Boolean privada) { this.privada = privada; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}
