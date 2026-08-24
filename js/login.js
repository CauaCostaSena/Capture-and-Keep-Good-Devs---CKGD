const inputUsuario = document.getElementById("input-usuario"); // e-mail
const inputSenha = document.getElementById("input-senha");
const botaoLogin = document.getElementById("btn-login");
const botaoCadastro = document.getElementById("btn-cadastro");

// Se já existe sessão ativa, vai direto para a home
if (CkgdAPI.isAutenticado()) {
    window.location.href = "home.html";
}

botaoCadastro.addEventListener("click", function () {
    window.location.href = "cadastro.html";
});

botaoLogin.addEventListener("click", async function () {
    const email = inputUsuario.value.trim();
    const senha = inputSenha.value;

    if (!email || !senha) {
        alert("Preencha e-mail e senha.");
        return;
    }

    botaoLogin.disabled = true;
    botaoLogin.textContent = "Entrando...";

    try {
        const auth = await CkgdAPI.login(email, senha);
        CkgdAPI.salvarSessao(auth);
        window.location.href = "home.html";
    } catch (err) {
        alert(err.message);
    } finally {
        botaoLogin.disabled = false;
        botaoLogin.textContent = "Entrar";
    }
});

// Permite logar com Enter
inputSenha.addEventListener("keydown", function (e) {
    if (e.key === "Enter") botaoLogin.click();
});
