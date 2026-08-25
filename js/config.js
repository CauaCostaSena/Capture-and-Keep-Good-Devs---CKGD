CkgdAPI.exigirAutenticacaoEmpresa();

const fotoPreview = document.getElementById("foto-preview");
const inputFoto = document.getElementById("input-foto");
const btnAlterarFoto = document.getElementById("btn-alterar-foto");

function aplicarDadosEmpresa(empresa) {
    document.getElementById("company-name").textContent = empresa.nomeEmpresa;
    document.getElementById("company-location").textContent =
        [empresa.cidade, empresa.estado, empresa.pais].filter(Boolean).join(", ");
    CkgdAPI.aplicarLogo(document.getElementById("company-logo"), empresa);
    CkgdAPI.aplicarLogo(fotoPreview, empresa);

    document.querySelector('.item-editable[data-field="nomeEmpresa"] [data-display]').textContent = empresa.nomeEmpresa;
    document.getElementById("item-localidade").textContent =
        [empresa.cidade, empresa.estado, empresa.pais].filter(Boolean).join(", ") || "Não informado";
    document.getElementById("item-plano").textContent = empresa.nomePlano || "—";
    document.getElementById("item-email").textContent = empresa.email;
    document.querySelector('.item-editable[data-field="telefone"] [data-display]').textContent = empresa.telefone || "Não informado";
}

async function carregarDados() {
    try {
        const empresa = await CkgdAPI.meusDados();
        aplicarDadosEmpresa(empresa);
    } catch (err) {
        document.getElementById("company-name").textContent = CkgdAPI.nomeEmpresaLogada() || "Minha Empresa";
    }
}

// --- Foto de perfil ---
btnAlterarFoto.addEventListener("click", () => inputFoto.click());

inputFoto.addEventListener("change", async () => {
    const arquivo = inputFoto.files[0];
    if (!arquivo) return;

    btnAlterarFoto.disabled = true;
    btnAlterarFoto.textContent = "Enviando...";

    try {
        const empresa = await CkgdAPI.atualizarFoto(arquivo);
        aplicarDadosEmpresa(empresa);
    } catch (err) {
        alert(err.message);
    } finally {
        btnAlterarFoto.disabled = false;
        btnAlterarFoto.textContent = "Alterar";
        inputFoto.value = "";
    }
});

// --- Itens editáveis (nome da empresa, telefone) ---
function configurarItemEditavel(item) {
    const field = item.dataset.field;
    const display = item.querySelector("[data-display]");
    const input = item.querySelector("[data-input]");
    const btnEdit = item.querySelector('[data-action="edit"]');
    const btnSave = item.querySelector('[data-action="save"]');
    const btnCancel = item.querySelector('[data-action="cancel"]');

    function entrarEmEdicao() {
        const valorAtual = display.textContent;
        input.value = (valorAtual === "Não informado" || valorAtual === "—") ? "" : valorAtual;
        display.hidden = true;
        input.hidden = false;
        btnEdit.hidden = true;
        btnSave.hidden = false;
        btnCancel.hidden = false;
        input.focus();
    }

    function sairDaEdicao() {
        display.hidden = false;
        input.hidden = true;
        btnEdit.hidden = false;
        btnSave.hidden = true;
        btnCancel.hidden = true;
    }

    async function salvar() {
        const novoValor = input.value.trim();

        if (field === "nomeEmpresa" && !novoValor) {
            alert("O nome da empresa não pode ficar em branco.");
            return;
        }

        btnSave.disabled = true;
        try {
            const empresa = await CkgdAPI.atualizarPerfil({ [field]: novoValor });
            aplicarDadosEmpresa(empresa);
            sairDaEdicao();
        } catch (err) {
            alert(err.message);
        } finally {
            btnSave.disabled = false;
        }
    }

    btnEdit.addEventListener("click", entrarEmEdicao);
    btnCancel.addEventListener("click", sairDaEdicao);
    btnSave.addEventListener("click", salvar);
    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") salvar();
        if (e.key === "Escape") sairDaEdicao();
    });
}

document.querySelectorAll(".item-editable").forEach(configurarItemEditavel);

document.getElementById("menu-suporte").addEventListener("click", () => {
    CkgdSuporte.abrirModal();
});

// --- Alterar senha ---
document.getElementById("menu-alterar-senha").addEventListener("click", () => {
    const overlay = document.createElement("div");
    overlay.className = "ckgd-modal-overlay";
    overlay.innerHTML = `
        <div class="ckgd-modal-box">
            <h3>Alterar senha</h3>
            <p class="ckgd-modal-subtitle">Informe sua senha atual e a nova senha (mínimo 6 caracteres).</p>
            <div class="ckgd-modal-field">
                <label for="senha-atual">Senha atual</label>
                <input type="password" id="senha-atual" autocomplete="current-password">
            </div>
            <div class="ckgd-modal-field">
                <label for="senha-nova">Nova senha</label>
                <input type="password" id="senha-nova" autocomplete="new-password" minlength="6">
            </div>
            <div class="ckgd-modal-field">
                <label for="senha-confirmar">Confirmar nova senha</label>
                <input type="password" id="senha-confirmar" autocomplete="new-password" minlength="6">
            </div>
            <p class="ckgd-modal-error" id="senha-erro"></p>
            <div class="ckgd-modal-actions">
                <button type="button" class="btn btn-secondary" id="senha-cancelar">Cancelar</button>
                <button type="button" class="btn btn-primary" id="senha-salvar">Salvar</button>
            </div>
        </div>
    `;
    document.body.appendChild(overlay);

    const inputAtual = overlay.querySelector("#senha-atual");
    const inputNova = overlay.querySelector("#senha-nova");
    const inputConfirmar = overlay.querySelector("#senha-confirmar");
    const erro = overlay.querySelector("#senha-erro");
    const btnSalvar = overlay.querySelector("#senha-salvar");

    function fechar() { overlay.remove(); }

    overlay.addEventListener("click", (e) => { if (e.target === overlay) fechar(); });
    overlay.querySelector("#senha-cancelar").addEventListener("click", fechar);

    btnSalvar.addEventListener("click", async () => {
        const senhaAtual = inputAtual.value;
        const novaSenha = inputNova.value;
        const confirmar = inputConfirmar.value;

        if (!senhaAtual || !novaSenha || !confirmar) {
            erro.textContent = "Preencha todos os campos.";
            return;
        }
        if (novaSenha.length < 6) {
            erro.textContent = "A nova senha deve ter ao menos 6 caracteres.";
            return;
        }
        if (novaSenha !== confirmar) {
            erro.textContent = "A confirmação não confere com a nova senha.";
            return;
        }

        erro.textContent = "";
        btnSalvar.disabled = true;
        btnSalvar.textContent = "Salvando...";

        try {
            await CkgdAPI.alterarSenha({ senhaAtual, novaSenha });
            overlay.querySelector(".ckgd-modal-box").innerHTML = `
                <h3>Senha alterada!</h3>
                <p class="ckgd-modal-subtitle">Sua senha foi atualizada com sucesso.</p>
                <div class="ckgd-modal-actions">
                    <button type="button" class="btn btn-primary" id="senha-fechar">Fechar</button>
                </div>
            `;
            overlay.querySelector("#senha-fechar").addEventListener("click", fechar);
        } catch (err) {
            erro.textContent = err.message;
            btnSalvar.disabled = false;
            btnSalvar.textContent = "Salvar";
        }
    });

    inputAtual.focus();
});

document.getElementById("menu-assinatura").addEventListener("click", async () => {
    try {
        const planos = await CkgdAPI.listarPlanos();
        const lista = planos.map(p => `• ${p.nomePlano} — ${Number(p.precoPlano) === 0 ? "Gratuito" : "R$ " + Number(p.precoPlano).toFixed(2)}`).join("\n");
        alert("Planos disponíveis:\n\n" + lista + "\n\nPara alterar de plano, entre em contato com o suporte.");
    } catch (err) {
        alert("Não foi possível carregar os planos.");
    }
});

document.getElementById("menu-sair").addEventListener("click", () => {
    CkgdAPI.encerrarSessao();
    window.location.href = "index.html";
});

carregarDados();
