package com.ckgd.dto;

public class AuthResponse {
    private String token;
    private String tipo; // EMPRESA ou CANDIDATO
    private String cnpj; // preenchido apenas para EMPRESA
    private Long nodeId; // preenchido apenas para CANDIDATO
    private String nome;
    private String email;

    public AuthResponse(String token, String tipo, String cnpj, Long nodeId, String nome, String email) {
        this.token = token;
        this.tipo = tipo;
        this.cnpj = cnpj;
        this.nodeId = nodeId;
        this.nome = nome;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
