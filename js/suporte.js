// ============================================================================
// CKGD - Modal de Suporte (compartilhado entre home, favoritos, perfil, config)
// ============================================================================

const CkgdSuporte = {

    abrirModal() {
        const overlay = document.createElement("div");
        overlay.className = "ckgd-modal-overlay";
        overlay.innerHTML = `
            <div class="ckgd-modal-box">
                <h3>Falar com o suporte</h3>
                <p class="ckgd-modal-subtitle">Envie sua dúvida ou solicitação. Nossa equipe responde pelo e-mail cadastrado na sua conta.</p>
                <div class="ckgd-modal-field">
                    <label for="suporte-assunto">Assunto</label>
                    <input type="text" id="suporte-assunto" maxlength="150" placeholder="Ex: Dúvida sobre meu plano">
                </div>
                <div class="ckgd-modal-field">
                    <label for="suporte-mensagem">Mensagem</label>
                    <textarea id="suporte-mensagem" placeholder="Descreva o que você precisa..."></textarea>
                </div>
                <p class="ckgd-modal-error" id="suporte-erro"></p>
                <div class="ckgd-modal-actions">
                    <button type="button" class="btn btn-secondary" id="suporte-cancelar">Cancelar</button>
                    <button type="button" class="btn btn-primary" id="suporte-enviar">Enviar</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);

        const inputAssunto = overlay.querySelector("#suporte-assunto");
        const inputMensagem = overlay.querySelector("#suporte-mensagem");
        const erro = overlay.querySelector("#suporte-erro");
        const btnEnviar = overlay.querySelector("#suporte-enviar");
        const btnCancelar = overlay.querySelector("#suporte-cancelar");

        function fechar() {
            overlay.remove();
        }

        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) fechar();
        });
        btnCancelar.addEventListener("click", fechar);

        btnEnviar.addEventListener("click", async () => {
            const assunto = inputAssunto.value.trim();
            const mensagem = inputMensagem.value.trim();

            if (!assunto || !mensagem) {
                erro.textContent = "Preencha o assunto e a mensagem.";
                return;
            }

            erro.textContent = "";
            btnEnviar.disabled = true;
            btnEnviar.textContent = "Enviando...";

            try {
                await CkgdAPI.enviarSuporte({ assunto, mensagem });
                overlay.querySelector(".ckgd-modal-box").innerHTML = `
                    <h3>Mensagem enviada!</h3>
                    <p class="ckgd-modal-subtitle">Recebemos sua solicitação e vamos responder o quanto antes.</p>
                    <div class="ckgd-modal-actions">
                        <button type="button" class="btn btn-primary" id="suporte-fechar">Fechar</button>
                    </div>
                `;
                overlay.querySelector("#suporte-fechar").addEventListener("click", fechar);
            } catch (err) {
                erro.textContent = err.message;
                btnEnviar.disabled = false;
                btnEnviar.textContent = "Enviar";
            }
        });

        inputAssunto.focus();
    }
};
