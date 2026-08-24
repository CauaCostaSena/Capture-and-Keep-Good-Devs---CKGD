package com.ckgd.security;

import com.ckgd.entity.Empresa;
import com.ckgd.repository.EmpresaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpresaUserDetailsService implements UserDetailsService {

    private final EmpresaRepository empresaRepository;

    public EmpresaUserDetailsService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Empresa empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Empresa não encontrada para o e-mail: " + email));
        return new EmpresaPrincipal(empresa);
    }
}
