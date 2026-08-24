package com.ckgd.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EmpresaCandidatoId implements Serializable {

    private String fkEmpresaCnpj;
    private Long fkCandidatoNodeId;

    public EmpresaCandidatoId() {}

    public EmpresaCandidatoId(String fkEmpresaCnpj, Long fkCandidatoNodeId) {
        this.fkEmpresaCnpj = fkEmpresaCnpj;
        this.fkCandidatoNodeId = fkCandidatoNodeId;
    }

    public String getFkEmpresaCnpj() { return fkEmpresaCnpj; }
    public void setFkEmpresaCnpj(String fkEmpresaCnpj) { this.fkEmpresaCnpj = fkEmpresaCnpj; }

    public Long getFkCandidatoNodeId() { return fkCandidatoNodeId; }
    public void setFkCandidatoNodeId(Long fkCandidatoNodeId) { this.fkCandidatoNodeId = fkCandidatoNodeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmpresaCandidatoId)) return false;
        EmpresaCandidatoId that = (EmpresaCandidatoId) o;
        return Objects.equals(fkEmpresaCnpj, that.fkEmpresaCnpj) &&
               Objects.equals(fkCandidatoNodeId, that.fkCandidatoNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkEmpresaCnpj, fkCandidatoNodeId);
    }
}
