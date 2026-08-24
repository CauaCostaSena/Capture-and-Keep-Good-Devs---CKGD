package com.ckgd.controller;

import com.ckgd.dto.CandidatoResponse;
import com.ckgd.service.CandidatoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<CandidatoResponse> perfil(@PathVariable Long nodeId, Authentication authentication) {
        String cnpj = (String) authentication.getPrincipal();
        return ResponseEntity.ok(candidatoService.buscarPerfilCompleto(nodeId, cnpj));
    }
}
