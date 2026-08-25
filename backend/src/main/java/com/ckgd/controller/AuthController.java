package com.ckgd.controller;

import com.ckgd.dto.AuthResponse;
import com.ckgd.dto.CadastroCandidatoRequest;
import com.ckgd.dto.CadastroEmpresaRequest;
import com.ckgd.dto.LoginRequest;
import com.ckgd.dto.RedefinirSenhaRequest;
import com.ckgd.service.CandidatoAuthService;
import com.ckgd.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmpresaService empresaService;
    private final CandidatoAuthService candidatoAuthService;

    public AuthController(EmpresaService empresaService, CandidatoAuthService candidatoAuthService) {
        this.empresaService = empresaService;
        this.candidatoAuthService = candidatoAuthService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroEmpresaRequest request) {
        AuthResponse response = empresaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/cadastro-candidato")
    public ResponseEntity<AuthResponse> cadastrarCandidato(@Valid @RequestBody CadastroCandidatoRequest request) {
        AuthResponse response = candidatoAuthService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login único para empresa e candidato: o e-mail decide qual das duas
     * contas será autenticada (não há sobreposição de e-mail entre os dois cadastros).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = empresaService.existePorEmail(request.getEmail())
                ? empresaService.login(request)
                : candidatoAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        empresaService.redefinirSenha(request);
        return ResponseEntity.noContent().build();
    }
}
