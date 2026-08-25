const form = document.getElementById("form-cadastro-candidato");
const botaoFinalizar = document.getElementById("btn-finalizar-cadastro");

// Se já existe sessão ativa, vai direto para a área correta
if (CkgdAPI.isAutenticado()) {
    window.location.href = CkgdAPI.isCandidatoLogado() ? "meu-perfil.html" : "home.html";
}

form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const payload = {
        nomeCandidato: document.getElementById("input-nome").value.trim(),
        usernameGithub: document.getElementById("input-username-github").value.trim(),
        email: document.getElementById("input-email").value.trim(),
        senha: document.getElementById("input-senha").value
    };

    botaoFinalizar.disabled = true;
    botaoFinalizar.textContent = "Cadastrando...";

    try {
        const auth = await CkgdAPI.cadastrarCandidato(payload);
        CkgdAPI.salvarSessao(auth);
        window.location.href = "meu-perfil.html";
    } catch (err) {
        alert(err.message);
    } finally {
        botaoFinalizar.disabled = false;
        botaoFinalizar.textContent = "Finalizar Cadastro";
    }
});
