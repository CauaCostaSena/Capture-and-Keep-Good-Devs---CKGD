const inputUsuario = document.getElementById("input-usuario");
const inputSenha = document.getElementById("input-senha");
const botaoLogin = document.getElementById("btn-login");


botaoLogin.addEventListener("click", function () {

    const usuario = inputUsuario.value;
    const senha = inputSenha.value;

    // Salvando no localStorage
    localStorage.setItem("loginUsuario", usuario);
    localStorage.setItem("loginSenha", senha); 
    alert("Login salvo no localStorage");
});
