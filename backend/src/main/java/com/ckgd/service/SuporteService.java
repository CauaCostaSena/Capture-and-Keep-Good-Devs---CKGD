package com.ckgd.service;

import com.ckgd.dto.SuporteRequest;
import com.ckgd.entity.Candidato;
import com.ckgd.entity.Empresa;
import com.ckgd.entity.SolicitacaoSuporte;
import com.ckgd.repository.CandidatoRepository;
import com.ckgd.repository.EmpresaRepository;
import com.ckgd.repository.SolicitacaoSuporteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuporteService {

    private final SolicitacaoSuporteRepository solicitacaoSuporteRepository;
    private final EmpresaRepository empresaRepository;
    private final CandidatoRepository candidatoRepository;

    public SuporteService(SolicitacaoSuporteRepository solicitacaoSuporteRepository,
                           EmpresaRepository empresaRepository,
                           CandidatoRepository candidatoRepository) {
        this.solicitacaoSuporteRepository = solicitacaoSuporteRepository;
        this.empresaRepository = empresaRepository;
        this.candidatoRepository = candidatoRepository;
    }

    @Transactional
    public SolicitacaoSuporte registrar(Authentication authentication, SuporteRequest request) {
        String principal = (String) authentication.getPrincipal();
        boolean isCandidato = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_CANDIDATO"::equals);

        SolicitacaoSuporte solicitacao = new SolicitacaoSuporte();
        solicitacao.setAssunto(request.getAssunto());
        solicitacao.setMensagem(request.getMensagem());

        if (isCandidato) {
            Candidato candidato = candidatoRepository.findById(Long.valueOf(principal))
                    .orElseThrow(() -> new IllegalStateException("Candidato autenticado não encontrado"));
            solicitacao.setTipoSolicitante("CANDIDATO");
            solicitacao.setNomeSolicitante(candidato.getNomeCandidato() != null ? candidato.getNomeCandidato() : candidato.getUsername());
            solicitacao.setEmailSolicitante(candidato.getEmail());
        } else {
            Empresa empresa = empresaRepository.findById(principal)
                    .orElseThrow(() -> new IllegalStateException("Empresa autenticada não encontrada"));
            solicitacao.setTipoSolicitante("EMPRESA");
            solicitacao.setNomeSolicitante(empresa.getNomeEmpresa());
            solicitacao.setEmailSolicitante(empresa.getEmail());
        }

        return solicitacaoSuporteRepository.save(solicitacao);
    }
}
