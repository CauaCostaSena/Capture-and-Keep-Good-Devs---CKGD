CkgdAPI.exigirAutenticacao();

const params = new URLSearchParams(window.location.search);
const nodeId = params.get("nodeId");

const companyName = document.getElementById("company-name");
const companyLocation = document.getElementById("company-location");
const companyLogo = document.getElementById("company-logo");

const perfilCarregando = document.getElementById("perfil-carregando");
const perfilErro = document.getElementById("perfil-erro");
const perfilErroMsg = document.getElementById("perfil-erro-msg");
const perfilContainer = document.getElementById("perfil-container");

async function carregarDadosEmpresa() {
    try {
        const empresa = await CkgdAPI.meusDados();
        companyName.textContent = empresa.nomeEmpresa;
        companyLocation.textContent = [empresa.cidade, empresa.estado, empresa.pais].filter(Boolean).join(", ");
        CkgdAPI.aplicarLogo(companyLogo, empresa);
    } catch (err) {
        companyName.textContent = CkgdAPI.nomeEmpresaLogada() || "Minha Empresa";
    }
}

function renderRepos(repos) {
    const lista = document.getElementById("perfil-repos-lista");
    lista.innerHTML = "";

    if (!repos || repos.length === 0) {
        lista.innerHTML = "<p style='color: var(--muted);'>Nenhum repositório público sincronizado.</p>";
        return;
    }

    repos.forEach(r => {
        const item = document.createElement("div");
        item.className = "repo-item";
        item.innerHTML = `
            <h4>${r.nomeRepositorio}</h4>
            <p>${r.descricao || "Sem descrição"}</p>
            <div class="repo-stats">
                <span><i class="fa-solid fa-star"></i> ${r.numeroEstrela}</span>
                <span><i class="fa-solid fa-code-fork"></i> ${r.numeroFork}</span>
                <span>${r.linguagemPrincipal || ""}</span>
            </div>
        `;
        lista.appendChild(item);
    });
}

async function carregarPerfil() {
    if (!nodeId) {
        perfilErroMsg.textContent = "Candidato não especificado.";
        perfilCarregando.style.display = "none";
        perfilErro.style.display = "flex";
        return;
    }

    try {
        const c = await CkgdAPI.perfilCandidato(nodeId);

        document.getElementById("perfil-avatar").src = c.avatarUrl || "https://avatars.githubusercontent.com/u/0?v=4";
        document.getElementById("perfil-nome").textContent = c.nomeCandidato || c.username;
        document.getElementById("perfil-username").textContent = "@" + c.username;

        const badges = document.getElementById("perfil-badges");
        badges.innerHTML = "";
        [c.linguagemPrincipal, c.localizacao].filter(Boolean).forEach(txt => {
            const span = document.createElement("span");
            span.textContent = txt;
            badges.appendChild(span);
        });

        document.getElementById("perfil-estrelas").textContent = c.totalEstrelas ?? 0;
        document.getElementById("perfil-repos").textContent = c.numRepositorios ?? (c.repositorios ? c.repositorios.length : 0);
        document.getElementById("perfil-linguagem").textContent = c.linguagemPrincipal || "—";
        document.getElementById("perfil-bio").textContent = c.bio || "Sem biografia informada.";
        document.getElementById("perfil-localizacao").textContent = c.localizacao || "Não informado";
        document.getElementById("perfil-github-link").href = "https://github.com/" + c.username;

        renderRepos(c.repositorios);

        document.getElementById("input-comentario").dataset.nodeId = c.nodeId;

        perfilCarregando.style.display = "none";
        perfilContainer.style.display = "block";
    } catch (err) {
        perfilErroMsg.textContent = err.message;
        perfilCarregando.style.display = "none";
        perfilErro.style.display = "flex";
    }
}

document.getElementById("btn-salvar-perfil").addEventListener("click", async () => {
    const comentario = document.getElementById("input-comentario").value.trim();
    const privada = document.getElementById("input-privada").checked;
    const btn = document.getElementById("btn-salvar-perfil");

    btn.disabled = true;
    btn.textContent = "Salvando...";

    try {
        await CkgdAPI.salvarAvaliacao(nodeId, {
            favorito: true,
            comentario: comentario || null,
            privada
        });
        alert("Perfil salvo nos favoritos com sucesso!");
    } catch (err) {
        alert(err.message);
    } finally {
        btn.disabled = false;
        btn.textContent = "Salvar Perfil";
    }
});

document.getElementById("btn-descartar").addEventListener("click", async () => {
    try {
        await CkgdAPI.removerFavorito(nodeId);
    } catch (err) {
        // segue mesmo se não havia favorito
    }
    window.location.href = "home.html";
});

document.getElementById("menu-sair").addEventListener("click", () => {
    CkgdAPI.encerrarSessao();
    window.location.href = "index.html";
});

document.getElementById("menu-suporte").addEventListener("click", () => {
    alert("Suporte: contato@ckgd.com — em breve um canal dedicado por aqui.");
});

carregarDadosEmpresa();
carregarPerfil();
