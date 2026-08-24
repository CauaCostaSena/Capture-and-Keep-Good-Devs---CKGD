const form = document.getElementById("form-recuperar");
const inputCnpj = document.getElementById("input-cnpj");
const inputEmail = document.getElementById("input-email");
const inputNovaSenha = document.getElementById("input-nova-senha");
const inputConfirmarSenha = document.getElementById("input-confirmar-senha");
const botaoRedefinir = document.getElementById("btn-redefinir");

form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const cnpj = inputCnpj.value.trim().replace(/\D/g, "");
    const email = inputEmail.value.trim();
    const novaSenha = inputNovaSenha.value;
    const confirmarSenha = inputConfirmarSenha.value;

    if (cnpj.length !== 14) {
        alert("O CNPJ deve conter exatamente 14 dígitos numéricos.");
        return;
    }

    if (novaSenha !== confirmarSenha) {
        alert("As senhas não coincidem.");
        return;
    }

    botaoRedefinir.disabled = true;
    botaoRedefinir.textContent = "Redefinindo...";

    try {
        await CkgdAPI.redefinirSenha({ cnpj, email, novaSenha });
        alert("Senha redefinida com sucesso! Faça login com a nova senha.");
        window.location.href = "index.html";
    } catch (err) {
        alert(err.message);
    } finally {
        botaoRedefinir.disabled = false;
        botaoRedefinir.textContent = "Redefinir senha";
    }
});
