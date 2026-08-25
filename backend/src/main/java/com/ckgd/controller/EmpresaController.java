package com.ckgd.controller;

import com.ckgd.dto.AlterarSenhaRequest;
import com.ckgd.dto.AtualizarEmpresaRequest;
import com.ckgd.dto.EmpresaResponse;
import com.ckgd.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/me")
    public ResponseEntity<EmpresaResponse> meusDados(Authentication authentication) {
        String cnpj = (String) authentication.getPrincipal();
        return ResponseEntity.ok(EmpresaResponse.from(empresaService.buscarPorCnpj(cnpj)));
    }

    @PutMapping("/me")
    public ResponseEntity<EmpresaResponse> atualizarPerfil(Authentication authentication,
                                                             @RequestBody AtualizarEmpresaRequest request) {
        String cnpj = (String) authentication.getPrincipal();
        return ResponseEntity.ok(EmpresaResponse.from(empresaService.atualizarPerfil(cnpj, request)));
    }

    @PostMapping(value = "/me/foto", consumes = "multipart/form-data")
    public ResponseEntity<EmpresaResponse> atualizarFoto(Authentication authentication,
                                                           @RequestParam("arquivo") MultipartFile arquivo) {
        String cnpj = (String) authentication.getPrincipal();
        return ResponseEntity.ok(EmpresaResponse.from(empresaService.atualizarFoto(cnpj, arquivo)));
    }

    @PutMapping("/me/senha")
    public ResponseEntity<Void> alterarSenha(Authentication authentication,
                                              @Valid @RequestBody AlterarSenhaRequest request) {
        String cnpj = (String) authentication.getPrincipal();
        empresaService.alterarSenha(cnpj, request);
        return ResponseEntity.noContent().build();
    }
}
