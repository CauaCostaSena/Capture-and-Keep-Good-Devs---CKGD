package com.ckgd.repository;

import com.ckgd.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, String> {
    Optional<Empresa> findByEmail(String email);
    boolean existsByEmail(String email);
}
