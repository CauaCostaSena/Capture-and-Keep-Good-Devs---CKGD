package com.ckgd.dto;

import com.ckgd.entity.Empresa;
import java.time.LocalDate;

public class EmpresaResponse {
    private String cnpj;
    private String nomeEmpresa;
    private String email;
    private String pais;
    private String estado;
    private String cidade;
    private String bairro;
    private String endereco;
    private String telefone;
    private String fotoUrl;
    private LocalDate dataCadastro;
    private String nomePlano;

    public static EmpresaResponse from(Empresa e) {
        EmpresaResponse dto = new EmpresaResponse();
        dto.cnpj = e.getCnpj();
        dto.nomeEmpresa = e.getNomeEmpresa();
        dto.email = e.getEmail();
        dto.pais = e.getPais();
        dto.estado = e.getEstado();
        dto.cidade = e.getCidade();
        dto.bairro = e.getBairro();
        dto.endereco = e.getEndereco();
        dto.telefone = e.getTelefone();
        dto.fotoUrl = e.getFotoUrl();
        dto.dataCadastro = e.getDataCadastro();
        dto.nomePlano = e.getPlano() != null ? e.getPlano().getNomePlano() : null;
        return dto;
    }

    public String getCnpj() { return cnpj; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getEmail() { return email; }
    public String getPais() { return pais; }
    public String getEstado() { return estado; }
    public String getCidade() { return cidade; }
    public String getBairro() { return bairro; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
    public String getFotoUrl() { return fotoUrl; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public String getNomePlano() { return nomePlano; }
}
