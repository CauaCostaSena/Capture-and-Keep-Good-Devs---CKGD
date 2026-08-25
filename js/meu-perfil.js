CkgdAPI.exigirAutenticacao();

if (!CkgdAPI.isCandidatoLogado()) {
    window.location.href = "home.html";
}

const perfilCarregando = document.getElementById("perfil-carregando");
const perfilErro = document.getElementById("perfil-erro");
const perfilErroMsg = document.getElementById("perfil-erro-msg");
const perfilContainer = document.getElementById("perfil-container");

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
    try {
        const c = await CkgdAPI.meuPerfilCandidato();

        const logo = document.getElementById("candidato-logo");
        if (c.avatarUrl) {
            logo.style.backgroundImage = `url('${c.avatarUrl}')`;
            logo.classList.add("has-photo");
            logo.textContent = "";
        } else {
            logo.textContent = (c.nomeCandidato || c.username).substring(0, 3).toUpperCase();
        }
        document.getElementById("candidato-nome").textContent = c.nomeCandidato || c.username;
        document.getElementById("candidato-username").textContent = "@" + c.username;

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

        perfilCarregando.style.display = "none";
        perfilContainer.style.display = "block";
    } catch (err) {
        perfilErroMsg.textContent = err.message;
        perfilCarregando.style.display = "none";
        perfilErro.style.display = "flex";
    }
}

document.getElementById("menu-sair").addEventListener("click", () => {
    CkgdAPI.encerrarSessao();
    window.location.href = "index.html";
});

document.getElementById("menu-suporte").addEventListener("click", () => {
    CkgdSuporte.abrirModal();
});

carregarPerfil();
