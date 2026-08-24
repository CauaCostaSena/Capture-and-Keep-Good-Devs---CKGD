package com.ckgd.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmpresaBuscaId implements Serializable {

    private Long fkBuscaIdBusca;
    private String fkEmpresaCnpj;

    public EmpresaBuscaId() {}

    public EmpresaBuscaId(Long fkBuscaIdBusca, String fkEmpresaCnpj) {
        this.fkBuscaIdBusca = fkBuscaIdBusca;
        this.fkEmpresaCnpj = fkEmpresaCnpj;
    }

    public Long getFkBuscaIdBusca() { return fkBuscaIdBusca; }
    public void setFkBuscaIdBusca(Long fkBuscaIdBusca) { this.fkBuscaIdBusca = fkBuscaIdBusca; }

    public String getFkEmpresaCnpj() { return fkEmpresaCnpj; }
    public void setFkEmpresaCnpj(String fkEmpresaCnpj) { this.fkEmpresaCnpj = fkEmpresaCnpj; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmpresaBuscaId)) return false;
        EmpresaBuscaId that = (EmpresaBuscaId) o;
        return Objects.equals(fkBuscaIdBusca, that.fkBuscaIdBusca) &&
               Objects.equals(fkEmpresaCnpj, that.fkEmpresaCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkBuscaIdBusca, fkEmpresaCnpj);
    }
}
