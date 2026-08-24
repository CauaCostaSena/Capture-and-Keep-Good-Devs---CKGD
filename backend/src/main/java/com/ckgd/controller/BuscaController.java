package com.ckgd.controller;

import com.ckgd.dto.CandidatoResponse;
import com.ckgd.service.BuscaService;
import com.ckgd.service.CandidatoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busca")
public class BuscaController {

    private final CandidatoService candidatoService;
    private final BuscaService buscaService;

    public BuscaController(CandidatoService candidatoService, BuscaService buscaService) {
        this.candidatoService = candidatoService;
        this.buscaService = buscaService;
    }

    @GetMapping
    public ResponseEntity<List<CandidatoResponse>> buscar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String linguagem,
            @RequestParam(required = false) String localizacao,
            Authentication authentication) {

        String cnpj = (String) authentication.getPrincipal();

        buscaService.validarLimiteDoPlano(cnpj);

        List<CandidatoResponse> resultado = candidatoService.buscarESincronizar(termo, linguagem, localizacao, cnpj);

        buscaService.registrarBusca(cnpj, termo, linguagem, localizacao);

        return ResponseEntity.ok(resultado);
    }
}
