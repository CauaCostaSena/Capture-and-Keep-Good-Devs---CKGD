package com.ckgd.repository;

import com.ckgd.entity.EmpresaCandidato;
import com.ckgd.entity.EmpresaCandidatoId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmpresaCandidatoRepository extends JpaRepository<EmpresaCandidato, EmpresaCandidatoId> {
    List<EmpresaCandidato> findByEmpresa_CnpjAndFavoritoTrue(String cnpj);
    Optional<EmpresaCandidato> findByEmpresa_CnpjAndCandidato_NodeId(String cnpj, Long nodeId);
    long countByEmpresa_CnpjAndFavoritoTrue(String cnpj);
}
