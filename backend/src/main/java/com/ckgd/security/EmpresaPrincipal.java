package com.ckgd.security;

import com.ckgd.entity.Empresa;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class EmpresaPrincipal implements UserDetails {

    private final Empresa empresa;

    public EmpresaPrincipal(Empresa empresa) {
        this.empresa = empresa;
    }

    public Empresa getEmpresa() { return empresa; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_EMPRESA"));
    }

    @Override
    public String getPassword() { return empresa.getSenha(); }

    @Override
    public String getUsername() { return empresa.getEmail(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
