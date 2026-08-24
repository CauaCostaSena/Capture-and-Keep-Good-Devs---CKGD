package com.ckgd.controller;

import com.ckgd.dto.AvaliacaoRequest;
import com.ckgd.dto.AvaliacaoResponse;
import com.ckgd.service.FavoritoAvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoAvaliacaoService favoritoService;

    public FavoritoController(FavoritoAvaliacaoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponse>> listar(Authentication authentication) {
        String cnpj = (String) authentication.getPrincipal();
        List<AvaliacaoResponse> resposta = favoritoService.listarFavoritos(cnpj).stream()
                .map(AvaliacaoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{nodeIdCandidato}")
    public ResponseEntity<AvaliacaoResponse> salvarAvaliacao(
            @PathVariable Long nodeIdCandidato,
            @Valid @RequestBody AvaliacaoRequest request,
            Authentication authentication) {
        String cnpj = (String) authentication.getPrincipal();
        var vinculo = favoritoService.salvarAvaliacao(cnpj, nodeIdCandidato, request);
        return ResponseEntity.ok(AvaliacaoResponse.from(vinculo));
    }

    @DeleteMapping("/{nodeIdCandidato}")
    public ResponseEntity<Void> remover(@PathVariable Long nodeIdCandidato, Authentication authentication) {
        String cnpj = (String) authentication.getPrincipal();
        favoritoService.removerFavorito(cnpj, nodeIdCandidato);
        return ResponseEntity.noContent().build();
    }
}
