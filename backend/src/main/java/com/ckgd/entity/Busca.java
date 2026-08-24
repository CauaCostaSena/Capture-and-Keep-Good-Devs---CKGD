package com.ckgd.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "busca")
public class Busca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_busca")
    private Long idBusca;

    @Column(name = "filtro_localizacao", length = 120)
    private String filtroLocalizacao;

    @Column(name = "filtro_linguagem", length = 60)
    private String filtroLinguagem;

    @Column(name = "termo_pesquisado", length = 150)
    private String termoPesquisado;

    @Column(name = "data_busca", nullable = false)
    private LocalDate dataBusca = LocalDate.now();

    @Column(name = "hora_busca", nullable = false)
    private LocalTime horaBusca = LocalTime.now();

    public Busca() {}

    // Getters e Setters
    public Long getIdBusca() { return idBusca; }
    public void setIdBusca(Long idBusca) { this.idBusca = idBusca; }

    public String getFiltroLocalizacao() { return filtroLocalizacao; }
    public void setFiltroLocalizacao(String filtroLocalizacao) { this.filtroLocalizacao = filtroLocalizacao; }

    public String getFiltroLinguagem() { return filtroLinguagem; }
    public void setFiltroLinguagem(String filtroLinguagem) { this.filtroLinguagem = filtroLinguagem; }

    public String getTermoPesquisado() { return termoPesquisado; }
    public void setTermoPesquisado(String termoPesquisado) { this.termoPesquisado = termoPesquisado; }

    public LocalDate getDataBusca() { return dataBusca; }
    public void setDataBusca(LocalDate dataBusca) { this.dataBusca = dataBusca; }

    public LocalTime getHoraBusca() { return horaBusca; }
    public void setHoraBusca(LocalTime horaBusca) { this.horaBusca = horaBusca; }
}
