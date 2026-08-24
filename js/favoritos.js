CkgdAPI.exigirAutenticacao();

const companyName = document.getElementById("company-name");
const companyLocation = document.getElementById("company-location");
const companyLogo = document.getElementById("company-logo");

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

async function carregarFavoritos() {
    const carregando = document.getElementById("favoritos-carregando");
    const vazio = document.getElementById("favoritos-vazio");
    const lista = document.getElementById("favoritos-lista");

    try {
        const favoritos = await CkgdAPI.listarFavoritos();
        carregando.style.display = "none";

        if (favoritos.length === 0) {
            vazio.style.display = "flex";
            return;
        }

        favoritos.forEach(f => {
            const card = document.createElement("div");
            card.className = "favorito-card";
            card.innerHTML = `
                <h3>${f.nomeCandidato || f.username}</h3>
                <p class="username">@${f.username}</p>
                ${f.privada ? '<span class="privada-tag">Avaliação privada</span>' : ''}
                ${f.comentario ? `<div class="comentario">${f.comentario}</div>` : ''}
                <a href="perfil.html?nodeId=${f.nodeIdCandidato}">Ver perfil completo →</a>
            `;
            lista.appendChild(card);
        });
    } catch (err) {
        carregando.style.display = "none";
        vazio.style.display = "flex";
        document.querySelector("#favoritos-vazio .status-text").textContent = err.message;
    }
}

document.getElementById("menu-sair").addEventListener("click", () => {
    CkgdAPI.encerrarSessao();
    window.location.href = "index.html";
});

document.getElementById("menu-suporte").addEventListener("click", () => {
    alert("Suporte: contato@ckgd.com — em breve um canal dedicado por aqui.");
});

carregarDadosEmpresa();
carregarFavoritos();
