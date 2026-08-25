package com.ckgd.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_suporte")
public class SolicitacaoSuporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Long idSolicitacao;

    @Column(name = "tipo_solicitante", nullable = false, length = 20)
    private String tipoSolicitante; // EMPRESA ou CANDIDATO

    @Column(name = "nome_solicitante", nullable = false, length = 150)
    private String nomeSolicitante;

    @Column(name = "email_solicitante", nullable = false, length = 150)
    private String emailSolicitante;

    @Column(name = "assunto", nullable = false, length = 150)
    private String assunto;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public SolicitacaoSuporte() {}

    public Long getIdSolicitacao() { return idSolicitacao; }
    public void setIdSolicitacao(Long idSolicitacao) { this.idSolicitacao = idSolicitacao; }

    public String getTipoSolicitante() { return tipoSolicitante; }
    public void setTipoSolicitante(String tipoSolicitante) { this.tipoSolicitante = tipoSolicitante; }

    public String getNomeSolicitante() { return nomeSolicitante; }
    public void setNomeSolicitante(String nomeSolicitante) { this.nomeSolicitante = nomeSolicitante; }

    public String getEmailSolicitante() { return emailSolicitante; }
    public void setEmailSolicitante(String emailSolicitante) { this.emailSolicitante = emailSolicitante; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
