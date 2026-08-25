CkgdAPI.exigirAutenticacaoEmpresa();

const inputTermo = document.getElementById("input-termo");
const btnBuscar = document.getElementById("btn-buscar");
const inputLocalizacao = document.getElementById("input-localizacao");
const langToggles = document.getElementById("lang-toggles");
const btnFiltrar = document.getElementById("btn-filtrar");
const btnLimparFiltros = document.getElementById("btn-limpar-filtros");

const estadoAguardando = document.getElementById("estado-aguardando");
const estadoCarregando = document.getElementById("estado-carregando");
const estadoErro = document.getElementById("estado-erro");
const erroMensagem = document.getElementById("erro-mensagem");
const estadoResultados = document.getElementById("estado-resultados");
const resultsList = document.getElementById("results-list");

const companyName = document.getElementById("company-name");
const companyLocation = document.getElementById("company-location");
const companyLogo = document.getElementById("company-logo");

let linguagemSelecionada = null;
let favoritosCache = new Set();

function mostrarEstado(estado) {
    estadoAguardando.style.display = estado === "aguardando" ? "flex" : "none";
    estadoCarregando.style.display = estado === "carregando" ? "flex" : "none";
    estadoErro.style.display = estado === "erro" ? "flex" : "none";
    estadoResultados.style.display = estado === "resultados" ? "flex" : "none";
}

async function carregarDadosEmpresa() {
    try {
        const empresa = await CkgdAPI.meusDados();
        companyName.textContent = empresa.nomeEmpresa;
        const partesLocal = [empresa.cidade, empresa.estado, empresa.pais].filter(Boolean);
        companyLocation.textContent = partesLocal.join(", ");
        CkgdAPI.aplicarLogo(companyLogo, empresa);
    } catch (err) {
        companyName.textContent = CkgdAPI.nomeEmpresaLogada() || "Minha Empresa";
    }
}

async function carregarFavoritosCache() {
    try {
        const favoritos = await CkgdAPI.listarFavoritos();
        favoritosCache = new Set(favoritos.filter(f => f.favorito).map(f => f.nodeIdCandidato));
    } catch (err) {
        // silencioso - favoritos são um extra visual, não bloqueiam a busca
    }
}

function renderizarResultados(candidatos) {
    resultsList.innerHTML = "";

    if (candidatos.length === 0) {
        resultsList.innerHTML = "<p style='color: var(--muted);'>Nenhum candidato encontrado. Tente outro termo ou ajuste os filtros.</p>";
        return;
    }

    candidatos.forEach(c => {
        const card = document.createElement("div");
        card.className = "candidato-card";

        const avatar = document.createElement("img");
        avatar.src = c.avatarUrl || "https://avatars.githubusercontent.com/u/0?v=4";
        avatar.alt = c.nomeCandidato || c.username;

        const info = document.createElement("div");
        info.className = "candidato-info";

        const nome = document.createElement("h3");
        nome.textContent = c.nomeCandidato || c.username;

        const tags = document.createElement("div");
        tags.className = "tags";
        const partes = [c.linguagemPrincipal, c.localizacao, (c.numRepositorios || 0) + " repositórios"].filter(Boolean);
        tags.textContent = partes.join(" • ");

        const actions = document.createElement("div");
        actions.className = "candidato-actions";

        const btnVerPerfil = document.createElement("a");
        btnVerPerfil.className = "btn-ver-perfil";
        btnVerPerfil.href = "perfil.html?nodeId=" + c.nodeId;
        btnVerPerfil.textContent = "Ver perfil";

        const btnFavoritar = document.createElement("button");
        btnFavoritar.className = "btn-favoritar" + (favoritosCache.has(c.nodeId) ? " ativo" : "");
        btnFavoritar.innerHTML = '<i class="fa-solid fa-star"></i>';
        btnFavoritar.title = "Favoritar candidato";
        btnFavoritar.addEventListener("click", async () => {
            const novoEstado = !favoritosCache.has(c.nodeId);
            try {
                await CkgdAPI.salvarAvaliacao(c.nodeId, { favorito: novoEstado });
                if (novoEstado) {
                    favoritosCache.add(c.nodeId);
                    btnFavoritar.classList.add("ativo");
                } else {
                    favoritosCache.delete(c.nodeId);
                    btnFavoritar.classList.remove("ativo");
                }
            } catch (err) {
                alert(err.message);
            }
        });

        actions.appendChild(btnVerPerfil);
        actions.appendChild(btnFavoritar);

        info.appendChild(nome);
        info.appendChild(tags);
        info.appendChild(actions);

        card.appendChild(avatar);
        card.appendChild(info);
        resultsList.appendChild(card);
    });
}

async function executarBusca() {
    const termo = inputTermo.value.trim();
    const localizacao = inputLocalizacao.value.trim();

    mostrarEstado("carregando");

    try {
        const candidatos = await CkgdAPI.buscarCandidatos({
            termo,
            linguagem: linguagemSelecionada,
            localizacao
        });
        await carregarFavoritosCache();
        renderizarResultados(candidatos);
        mostrarEstado("resultados");
    } catch (err) {
        erroMensagem.textContent = err.message;
        mostrarEstado("erro");
    }
}

btnBuscar.addEventListener("click", executarBusca);
inputTermo.addEventListener("keydown", (e) => { if (e.key === "Enter") executarBusca(); });

langToggles.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-lang]");
    if (!btn) return;

    const jaSelecionado = btn.classList.contains("selected");
    [...langToggles.children].forEach(b => b.classList.remove("selected"));

    linguagemSelecionada = jaSelecionado ? null : btn.dataset.lang;
    if (!jaSelecionado) btn.classList.add("selected");
});

btnFiltrar.addEventListener("click", executarBusca);

btnLimparFiltros.addEventListener("click", () => {
    linguagemSelecionada = null;
    inputLocalizacao.value = "";
    [...langToggles.children].forEach(b => b.classList.remove("selected"));
    executarBusca();
});

document.getElementById("menu-sair").addEventListener("click", () => {
    CkgdAPI.encerrarSessao();
    window.location.href = "index.html";
});

document.getElementById("menu-suporte").addEventListener("click", () => {
    CkgdSuporte.abrirModal();
});

carregarDadosEmpresa();
