package com.ckgd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroCandidatoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nomeCandidato;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
    private String senha;

    @NotBlank(message = "Usuário do GitHub é obrigatório")
    private String usernameGithub;

    public String getNomeCandidato() { return nomeCandidato; }
    public void setNomeCandidato(String nomeCandidato) { this.nomeCandidato = nomeCandidato; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getUsernameGithub() { return usernameGithub; }
    public void setUsernameGithub(String usernameGithub) { this.usernameGithub = usernameGithub; }
}
