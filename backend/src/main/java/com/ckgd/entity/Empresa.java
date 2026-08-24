package com.ckgd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @Column(name = "cnpj", length = 14)
    private String cnpj;

    @Column(name = "nome_empresa", nullable = false, length = 120)
    private String nomeEmpresa;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha", nullable = false)
    @JsonIgnore
    private String senha; // hash BCrypt

    @Column(name = "pais", length = 60)
    private String pais;

    @Column(name = "estado", length = 60)
    private String estado;

    @Column(name = "cidade", length = 60)
    private String cidade;

    @Column(name = "bairro", length = 60)
    private String bairro;

    @Column(name = "endereco", length = 150)
    private String endereco;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_plano_id_plano", nullable = false)
    private PlanoDeAssinatura plano;

    public Empresa() {}

    // Getters e Setters
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public PlanoDeAssinatura getPlano() { return plano; }
    public void setPlano(PlanoDeAssinatura plano) { this.plano = plano; }
}
