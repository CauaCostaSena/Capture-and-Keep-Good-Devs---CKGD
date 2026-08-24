package com.ckgd.controller;

import com.ckgd.entity.PlanoDeAssinatura;
import com.ckgd.repository.PlanoDeAssinaturaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/planos")
public class PlanoController {

    private final PlanoDeAssinaturaRepository planoRepository;

    public PlanoController(PlanoDeAssinaturaRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    @GetMapping
    public ResponseEntity<List<PlanoDeAssinatura>> listar() {
        return ResponseEntity.ok(planoRepository.findAll());
    }
}
