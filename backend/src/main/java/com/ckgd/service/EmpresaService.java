package com.ckgd.service;

import com.ckgd.dto.AlterarSenhaRequest;
import com.ckgd.dto.AtualizarEmpresaRequest;
import com.ckgd.dto.AuthResponse;
import com.ckgd.dto.CadastroEmpresaRequest;
import com.ckgd.dto.LoginRequest;
import com.ckgd.dto.RedefinirSenhaRequest;
import com.ckgd.entity.Empresa;
import com.ckgd.entity.PlanoDeAssinatura;
import com.ckgd.exception.BusinessException;
import com.ckgd.exception.ResourceNotFoundException;
import com.ckgd.repository.EmpresaRepository;
import com.ckgd.repository.PlanoDeAssinaturaRepository;
import com.ckgd.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Service
public class EmpresaService {

    private static final Set<String> TIPOS_IMAGEM_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final EmpresaRepository empresaRepository;
    private final PlanoDeAssinaturaRepository planoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${ckgd.upload.dir:uploads}")
    private String uploadDir;

    public EmpresaService(EmpresaRepository empresaRepository,
                           PlanoDeAssinaturaRepository planoRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.empresaRepository = empresaRepository;
        this.planoRepository = planoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse cadastrar(CadastroEmpresaRequest req) {
        if (empresaRepository.existsById(req.getCnpj())) {
            throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ");
        }
        if (empresaRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Já existe uma empresa cadastrada com este e-mail");
        }

        PlanoDeAssinatura plano;
        if (req.getIdPlano() != null) {
            plano = planoRepository.findById(req.getIdPlano())
                    .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));
        } else {
            // Plano padrão gratuito: assume-se o de menor id (Free), criado no seed inicial
            plano = planoRepository.findAll().stream()
                    .min((a, b) -> a.getIdPlano().compareTo(b.getIdPlano()))
                    .orElseThrow(() -> new BusinessException("Nenhum plano de assinatura cadastrado no sistema"));
        }

        Empresa empresa = new Empresa();
        empresa.setCnpj(req.getCnpj());
        empresa.setNomeEmpresa(req.getNomeEmpresa());
        empresa.setEmail(req.getEmail());
        empresa.setSenha(passwordEncoder.encode(req.getSenha()));
        empresa.setPais(req.getPais());
        empresa.setEstado(req.getEstado());
        empresa.setCidade(req.getCidade());
        empresa.setBairro(req.getBairro());
        empresa.setEndereco(req.getEndereco());
        empresa.setPlano(plano);

        empresa = empresaRepository.save(empresa);

        String token = jwtUtil.gerarToken(empresa.getCnpj(), "EMPRESA");
        return new AuthResponse(token, "EMPRESA", empresa.getCnpj(), null, empresa.getNomeEmpresa(), empresa.getEmail());
    }

    public boolean existePorEmail(String email) {
        return empresaRepository.existsByEmail(email);
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("E-mail ou senha incorretos");
        }

        Empresa empresa = empresaRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        String token = jwtUtil.gerarToken(empresa.getCnpj(), "EMPRESA");
        return new AuthResponse(token, "EMPRESA", empresa.getCnpj(), null, empresa.getNomeEmpresa(), empresa.getEmail());
    }

    public Empresa buscarPorCnpj(String cnpj) {
        return empresaRepository.findById(cnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
    }

    @Transactional
    public Empresa atualizarPerfil(String cnpj, AtualizarEmpresaRequest req) {
        Empresa empresa = buscarPorCnpj(cnpj);

        if (StringUtils.hasText(req.getNomeEmpresa())) {
            empresa.setNomeEmpresa(req.getNomeEmpresa().trim());
        }
        if (req.getTelefone() != null) {
            String telefone = req.getTelefone().trim();
            empresa.setTelefone(telefone.isEmpty() ? null : telefone);
        }

        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa atualizarFoto(String cnpj, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado");
        }
        if (!TIPOS_IMAGEM_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new BusinessException("Formato de imagem inválido. Use JPEG, PNG ou WEBP");
        }

        Empresa empresa = buscarPorCnpj(cnpj);

        String extensao = switch (arquivo.getContentType()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        try {
            Path pastaDestino = Paths.get(uploadDir, "empresas");
            Files.createDirectories(pastaDestino);

            // Remove fotos antigas da mesma empresa com outra extensão, para não deixar órfãs
            for (String ext : new String[]{".jpg", ".png", ".webp"}) {
                Files.deleteIfExists(pastaDestino.resolve(cnpj + ext));
            }

            Path destino = pastaDestino.resolve(cnpj + extensao);
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar a foto de perfil", e);
        }

        empresa.setFotoUrl("/uploads/empresas/" + cnpj + extensao);
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void alterarSenha(String cnpj, AlterarSenhaRequest req) {
        Empresa empresa = buscarPorCnpj(cnpj);

        if (!passwordEncoder.matches(req.getSenhaAtual(), empresa.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        empresa.setSenha(passwordEncoder.encode(req.getNovaSenha()));
        empresaRepository.save(empresa);
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaRequest req) {
        Empresa empresa = empresaRepository.findById(req.getCnpj())
                .orElseThrow(() -> new BusinessException("CNPJ ou e-mail não conferem com nenhuma empresa cadastrada"));

        if (!empresa.getEmail().equalsIgnoreCase(req.getEmail())) {
            throw new BusinessException("CNPJ ou e-mail não conferem com nenhuma empresa cadastrada");
        }

        empresa.setSenha(passwordEncoder.encode(req.getNovaSenha()));
        empresaRepository.save(empresa);
    }
}
