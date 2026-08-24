CkgdAPI.exigirAutenticacao();

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
    alert("Suporte: contato@ckgd.com — em breve um canal dedicado por aqui.");
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
