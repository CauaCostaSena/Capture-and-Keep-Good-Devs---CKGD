package com.ckgd.repository;

import com.ckgd.entity.EmpresaBusca;
import com.ckgd.entity.EmpresaBuscaId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpresaBuscaRepository extends JpaRepository<EmpresaBusca, EmpresaBuscaId> {
    long countByEmpresa_Cnpj(String cnpj);
    List<EmpresaBusca> findByEmpresa_Cnpj(String cnpj);
}
