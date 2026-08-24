package com.ckgd.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "plano_de_assinatura")
public class PlanoDeAssinatura {

    public enum Periodicidade { MENSAL, ANUAL }
    public enum StatusPlano { ATIVO, INATIVO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano")
    private Integer idPlano;

    @Column(name = "nome_plano", nullable = false, length = 60)
    private String nomePlano;

    @Column(name = "preco_plano", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoPlano;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade", nullable = false)
    private Periodicidade periodicidade = Periodicidade.MENSAL;

    @Column(name = "limite_requisicao", nullable = false)
    private Integer limiteRequisicao = 0;

    @Column(name = "limite_avaliacao", nullable = false)
    private Integer limiteAvaliacao = 0;

    @Column(name = "limite_comparacao", nullable = false)
    private Integer limiteComparacao = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_plano", nullable = false)
    private StatusPlano statusPlano = StatusPlano.ATIVO;

    @Column(name = "data_ativacao")
    private LocalDate dataAtivacao;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    public PlanoDeAssinatura() {}

    // Getters e Setters
    public Integer getIdPlano() { return idPlano; }
    public void setIdPlano(Integer idPlano) { this.idPlano = idPlano; }

    public String getNomePlano() { return nomePlano; }
    public void setNomePlano(String nomePlano) { this.nomePlano = nomePlano; }

    public BigDecimal getPrecoPlano() { return precoPlano; }
    public void setPrecoPlano(BigDecimal precoPlano) { this.precoPlano = precoPlano; }

    public Periodicidade getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(Periodicidade periodicidade) { this.periodicidade = periodicidade; }

    public Integer getLimiteRequisicao() { return limiteRequisicao; }
    public void setLimiteRequisicao(Integer limiteRequisicao) { this.limiteRequisicao = limiteRequisicao; }

    public Integer getLimiteAvaliacao() { return limiteAvaliacao; }
    public void setLimiteAvaliacao(Integer limiteAvaliacao) { this.limiteAvaliacao = limiteAvaliacao; }

    public Integer getLimiteComparacao() { return limiteComparacao; }
    public void setLimiteComparacao(Integer limiteComparacao) { this.limiteComparacao = limiteComparacao; }

    public StatusPlano getStatusPlano() { return statusPlano; }
    public void setStatusPlano(StatusPlano statusPlano) { this.statusPlano = statusPlano; }

    public LocalDate getDataAtivacao() { return dataAtivacao; }
    public void setDataAtivacao(LocalDate dataAtivacao) { this.dataAtivacao = dataAtivacao; }

    public LocalDate getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDate dataExpiracao) { this.dataExpiracao = dataExpiracao; }
}
