package com.ckgd.dto;

public class AuthResponse {
    private String token;
    private String cnpj;
    private String nomeEmpresa;
    private String email;

    public AuthResponse(String token, String cnpj, String nomeEmpresa, String email) {
        this.token = token;
        this.cnpj = cnpj;
        this.nomeEmpresa = nomeEmpresa;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
