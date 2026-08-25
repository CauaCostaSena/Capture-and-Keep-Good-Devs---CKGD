// ============================================================================
// CKGD - Cliente de API compartilhado
// ============================================================================

const API_BASE_URL = "http://localhost:8080/api";

const CkgdAPI = {

    _token() {
        return localStorage.getItem("ckgd_token");
    },

    isAutenticado() {
        return !!this._token();
    },

    tipoLogado() {
        return localStorage.getItem("ckgd_tipo") || "EMPRESA";
    },

    isCandidatoLogado() {
        return this.tipoLogado() === "CANDIDATO";
    },

    salvarSessao(auth) {
        localStorage.setItem("ckgd_token", auth.token);
        localStorage.setItem("ckgd_tipo", auth.tipo || "EMPRESA");
        localStorage.setItem("ckgd_cnpj", auth.cnpj || "");
        localStorage.setItem("ckgd_nodeId", auth.nodeId != null ? String(auth.nodeId) : "");
        localStorage.setItem("ckgd_nome", auth.nome || "");
        localStorage.setItem("ckgd_email", auth.email || "");
    },

    encerrarSessao() {
        localStorage.removeItem("ckgd_token");
        localStorage.removeItem("ckgd_tipo");
        localStorage.removeItem("ckgd_cnpj");
        localStorage.removeItem("ckgd_nodeId");
        localStorage.removeItem("ckgd_nome");
        localStorage.removeItem("ckgd_email");
    },

    nomeEmpresaLogada() {
        return localStorage.getItem("ckgd_nome") || "";
    },

    /** Monta a URL absoluta de um arquivo servido pelo backend (ex: foto de perfil). */
    urlArquivo(caminhoRelativo) {
        if (!caminhoRelativo) return null;
        return API_BASE_URL.replace(/\/api\/?$/, "") + caminhoRelativo;
    },

    /** Aplica a foto de perfil (ou as iniciais, como fallback) num elemento .logo. */
    aplicarLogo(elLogo, empresa) {
        const url = this.urlArquivo(empresa.fotoUrl);
        if (url) {
            elLogo.style.backgroundImage = `url('${url}')`;
            elLogo.classList.add("has-photo");
            elLogo.textContent = "";
        } else {
            elLogo.style.backgroundImage = "";
            elLogo.classList.remove("has-photo");
            elLogo.textContent = empresa.nomeEmpresa.substring(0, 3).toUpperCase();
        }
    },

    /** Redireciona para o login caso não haja sessão ativa. Use no topo de páginas protegidas. */
    exigirAutenticacao() {
        if (!this.isAutenticado()) {
            window.location.href = "index.html";
        }
    },

    /** Como exigirAutenticacao(), mas também manda o candidato de volta para a área dele. Use nas páginas exclusivas de empresa. */
    exigirAutenticacaoEmpresa() {
        this.exigirAutenticacao();
        if (this.isCandidatoLogado()) {
            window.location.href = "meu-perfil.html";
        }
    },

    async _request(method, path, body) {
        const headers = { "Content-Type": "application/json" };
        const token = this._token();
        if (token) headers["Authorization"] = "Bearer " + token;

        let response;
        try {
            response = await fetch(API_BASE_URL + path, {
                method,
                headers,
                body: body !== undefined ? JSON.stringify(body) : undefined
            });
        } catch (networkError) {
            throw new Error("Não foi possível conectar ao servidor. Verifique se o backend está rodando em " + API_BASE_URL);
        }

        if (response.status === 204) return null;

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const mensagem = (data && (data.mensagem || data.message)) || "Erro na requisição";
            throw new Error(mensagem);
        }

        return data;
    },

    // --- Autenticação ---
    login(email, senha) {
        return this._request("POST", "/auth/login", { email, senha });
    },
    cadastrar(payload) {
        return this._request("POST", "/auth/cadastro", payload);
    },
    cadastrarCandidato(payload) {
        return this._request("POST", "/auth/cadastro-candidato", payload);
    },

    // --- Empresa ---
    meusDados() {
        return this._request("GET", "/empresas/me");
    },
    atualizarPerfil(payload) {
        return this._request("PUT", "/empresas/me", payload);
    },
    alterarSenha(payload) {
        return this._request("PUT", "/empresas/me/senha", payload);
    },
    async atualizarFoto(arquivo) {
        const formData = new FormData();
        formData.append("arquivo", arquivo);

        const headers = {};
        const token = this._token();
        if (token) headers["Authorization"] = "Bearer " + token;

        let response;
        try {
            response = await fetch(API_BASE_URL + "/empresas/me/foto", {
                method: "POST",
                headers,
                body: formData
            });
        } catch (networkError) {
            throw new Error("Não foi possível conectar ao servidor. Verifique se o backend está rodando em " + API_BASE_URL);
        }

        const data = await response.json().catch(() => null);
        if (!response.ok) {
            const mensagem = (data && (data.mensagem || data.message)) || "Erro ao enviar a foto";
            throw new Error(mensagem);
        }
        return data;
    },

    // --- Recuperação de senha ---
    redefinirSenha(payload) {
        return this._request("POST", "/auth/redefinir-senha", payload);
    },

    // --- Planos ---
    listarPlanos() {
        return this._request("GET", "/planos");
    },

    // --- Busca de candidatos ---
    buscarCandidatos({ termo, linguagem, localizacao }) {
        const params = new URLSearchParams();
        if (termo) params.set("termo", termo);
        if (linguagem) params.set("linguagem", linguagem);
        if (localizacao) params.set("localizacao", localizacao);
        return this._request("GET", "/busca?" + params.toString());
    },

    // --- Perfil de candidato ---
    perfilCandidato(nodeId) {
        return this._request("GET", "/candidatos/" + nodeId);
    },
    meuPerfilCandidato() {
        return this._request("GET", "/candidatos/me");
    },

    // --- Favoritos / avaliações ---
    listarFavoritos() {
        return this._request("GET", "/favoritos");
    },
    salvarAvaliacao(nodeId, payload) {
        return this._request("PUT", "/favoritos/" + nodeId, payload);
    },
    removerFavorito(nodeId) {
        return this._request("DELETE", "/favoritos/" + nodeId);
    },

    // --- Suporte ---
    enviarSuporte(payload) {
        return this._request("POST", "/suporte", payload);
    }
};
