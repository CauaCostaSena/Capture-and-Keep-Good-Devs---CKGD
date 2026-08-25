const form = document.getElementById("form-cadastro");
const selectPlano = document.getElementById("input-plano");
const botaoFinalizar = document.getElementById("btn-finalizar-cadastro");

// Se já existe sessão ativa, vai direto para a home
if (CkgdAPI.isAutenticado()) {
    window.location.href = "home.html";
}

async function carregarPlanos() {
    try {
        const planos = await CkgdAPI.listarPlanos();
        selectPlano.innerHTML = "";
        planos.forEach(plano => {
            const option = document.createElement("option");
            option.value = plano.idPlano;
            const preco = Number(plano.precoPlano) === 0 ? "Gratuito" : `R$ ${Number(plano.precoPlano).toFixed(2)}/${plano.periodicidade === "MENSAL" ? "mês" : "ano"}`;
            option.textContent = `${plano.nomePlano} — ${preco}`;
            selectPlano.appendChild(option);
        });
    } catch (err) {
        selectPlano.innerHTML = '<option value="">Não foi possível carregar os planos</option>';
    }
}

carregarPlanos();

form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const payload = {
        nomeEmpresa: document.getElementById("input-nome-empresa").value.trim(),
        cnpj: document.getElementById("input-cnpj").value.trim().replace(/\D/g, ""),
        email: document.getElementById("input-email").value.trim(),
        senha: document.getElementById("input-senha").value,
        pais: document.getElementById("input-pais").value.trim(),
        estado: document.getElementById("input-estado").value.trim(),
        cidade: document.getElementById("input-cidade").value.trim(),
        bairro: document.getElementById("input-bairro").value.trim(),
        endereco: document.getElementById("input-endereco").value.trim(),
        idPlano: selectPlano.value ? Number(selectPlano.value) : null
    };

    if (payload.cnpj.length !== 14) {
        alert("O CNPJ deve conter exatamente 14 dígitos numéricos.");
        return;
    }

    botaoFinalizar.disabled = true;
    botaoFinalizar.textContent = "Cadastrando...";

    try {
        const auth = await CkgdAPI.cadastrar(payload);
        CkgdAPI.salvarSessao(auth);
        window.location.href = "home.html";
    } catch (err) {
        alert(err.message);
    } finally {
        botaoFinalizar.disabled = false;
        botaoFinalizar.textContent = "Finalizar Cadastro";
    }
});
