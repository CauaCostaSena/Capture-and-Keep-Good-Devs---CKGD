package com.ckgd.controller;

import com.ckgd.dto.AuthResponse;
import com.ckgd.dto.CadastroEmpresaRequest;
import com.ckgd.dto.LoginRequest;
import com.ckgd.dto.RedefinirSenhaRequest;
import com.ckgd.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmpresaService empresaService;

    public AuthController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroEmpresaRequest request) {
        AuthResponse response = empresaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = empresaService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        empresaService.redefinirSenha(request);
        return ResponseEntity.noContent().build();
    }
}
