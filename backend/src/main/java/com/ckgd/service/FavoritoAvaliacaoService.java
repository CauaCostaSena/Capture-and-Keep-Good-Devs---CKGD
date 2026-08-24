package com.ckgd.service;

import com.ckgd.dto.AvaliacaoRequest;
import com.ckgd.entity.Candidato;
import com.ckgd.entity.Empresa;
import com.ckgd.entity.EmpresaCandidato;
import com.ckgd.exception.ResourceNotFoundException;
import com.ckgd.repository.CandidatoRepository;
import com.ckgd.repository.EmpresaCandidatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoAvaliacaoService {

    private final EmpresaCandidatoRepository empresaCandidatoRepository;
    private final CandidatoRepository candidatoRepository;
    private final EmpresaService empresaService;

    public FavoritoAvaliacaoService(EmpresaCandidatoRepository empresaCandidatoRepository,
                                     CandidatoRepository candidatoRepository,
                                     EmpresaService empresaService) {
        this.empresaCandidatoRepository = empresaCandidatoRepository;
        this.candidatoRepository = candidatoRepository;
        this.empresaService = empresaService;
    }

    @Transactional
    public EmpresaCandidato salvarAvaliacao(String cnpj, Long nodeIdCandidato, AvaliacaoRequest req) {
        Empresa empresa = empresaService.buscarPorCnpj(cnpj);
        Candidato candidato = candidatoRepository.findById(nodeIdCandidato)
                .orElseThrow(() -> new ResourceNotFoundException("Candidato não encontrado"));

        EmpresaCandidato vinculo = empresaCandidatoRepository
                .findByEmpresa_CnpjAndCandidato_NodeId(cnpj, nodeIdCandidato)
                .orElseGet(() -> new EmpresaCandidato(empresa, candidato));

        if (req.getFavorito() != null) {
            vinculo.setFavorito(req.getFavorito());
        }
        if (req.getComentario() != null) {
            vinculo.setComentario(req.getComentario());
            vinculo.setDataAvaliacao(LocalDateTime.now());
        }
        if (req.getPrivada() != null) {
            vinculo.setPrivada(req.getPrivada());
        }

        return empresaCandidatoRepository.save(vinculo);
    }

    @Transactional
    public void removerFavorito(String cnpj, Long nodeIdCandidato) {
        empresaCandidatoRepository.findByEmpresa_CnpjAndCandidato_NodeId(cnpj, nodeIdCandidato)
                .ifPresent(v -> {
                    v.setFavorito(false);
                    empresaCandidatoRepository.save(v);
                });
    }

    public List<EmpresaCandidato> listarFavoritos(String cnpj) {
        return empresaCandidatoRepository.findByEmpresa_CnpjAndFavoritoTrue(cnpj);
    }
}
