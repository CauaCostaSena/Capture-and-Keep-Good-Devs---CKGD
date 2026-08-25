package com.ckgd.controller;

import com.ckgd.dto.SuporteRequest;
import com.ckgd.service.SuporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suporte")
public class SuporteController {

    private final SuporteService suporteService;

    public SuporteController(SuporteService suporteService) {
        this.suporteService = suporteService;
    }

    @PostMapping
    public ResponseEntity<Void> enviar(Authentication authentication, @Valid @RequestBody SuporteRequest request) {
        suporteService.registrar(authentication, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
