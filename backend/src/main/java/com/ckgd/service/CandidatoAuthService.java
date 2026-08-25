package com.ckgd.service;

import com.ckgd.dto.AuthResponse;
import com.ckgd.dto.CadastroCandidatoRequest;
import com.ckgd.dto.LoginRequest;
import com.ckgd.entity.Candidato;
import com.ckgd.exception.BusinessException;
import com.ckgd.repository.CandidatoRepository;
import com.ckgd.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidatoAuthService {

    private final CandidatoRepository candidatoRepository;
    private final CandidatoService candidatoService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public CandidatoAuthService(CandidatoRepository candidatoRepository,
                                 CandidatoService candidatoService,
                                 PasswordEncoder passwordEncoder,
                                 JwtUtil jwtUtil) {
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public boolean existePorEmail(String email) {
        return candidatoRepository.existsByEmail(email);
    }

    @Transactional
    public AuthResponse cadastrar(CadastroCandidatoRequest req) {
        if (candidatoRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Já existe um candidato cadastrado com este e-mail");
        }

        Candidato candidato = candidatoService.sincronizarPorUsername(req.getUsernameGithub());

        if (candidato.getEmail() != null && !candidato.getEmail().equalsIgnoreCase(req.getEmail())
                && candidato.getSenha() != null) {
            throw new BusinessException("Este usuário do GitHub já possui uma conta cadastrada");
        }

        candidato.setNomeCandidato(req.getNomeCandidato());
        candidato.setEmail(req.getEmail());
        candidato.setSenha(passwordEncoder.encode(req.getSenha()));
        candidato = candidatoRepository.save(candidato);

        String token = jwtUtil.gerarToken(String.valueOf(candidato.getNodeId()), "CANDIDATO");
        return new AuthResponse(token, "CANDIDATO", null, candidato.getNodeId(), candidato.getNomeCandidato(), candidato.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        Candidato candidato = candidatoRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha incorretos"));

        if (candidato.getSenha() == null || !passwordEncoder.matches(req.getSenha(), candidato.getSenha())) {
            throw new BadCredentialsException("E-mail ou senha incorretos");
        }

        String token = jwtUtil.gerarToken(String.valueOf(candidato.getNodeId()), "CANDIDATO");
        return new AuthResponse(token, "CANDIDATO", null, candidato.getNodeId(), candidato.getNomeCandidato(), candidato.getEmail());
    }
}
