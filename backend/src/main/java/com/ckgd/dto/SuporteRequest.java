package com.ckgd.dto;

import jakarta.validation.constraints.NotBlank;

public class SuporteRequest {

    @NotBlank(message = "Assunto é obrigatório")
    private String assunto;

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
