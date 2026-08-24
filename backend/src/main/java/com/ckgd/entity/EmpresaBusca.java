package com.ckgd.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa_busca")
public class EmpresaBusca {

    @EmbeddedId
    private EmpresaBuscaId id = new EmpresaBuscaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fkBuscaIdBusca")
    @JoinColumn(name = "fk_busca_id_busca")
    private Busca busca;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fkEmpresaCnpj")
    @JoinColumn(name = "fk_empresa_cnpj")
    private Empresa empresa;

    public EmpresaBusca() {}

    public EmpresaBusca(Busca busca, Empresa empresa) {
        this.busca = busca;
        this.empresa = empresa;
        this.id = new EmpresaBuscaId(busca.getIdBusca(), empresa.getCnpj());
    }

    public EmpresaBuscaId getId() { return id; }
    public void setId(EmpresaBuscaId id) { this.id = id; }

    public Busca getBusca() { return busca; }
    public void setBusca(Busca busca) { this.busca = busca; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
