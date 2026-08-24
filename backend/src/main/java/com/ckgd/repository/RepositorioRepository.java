package com.ckgd.repository;

import com.ckgd.entity.Repositorio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioRepository extends JpaRepository<Repositorio, String> {
    List<Repositorio> findByCandidato_NodeId(Long nodeId);
}
