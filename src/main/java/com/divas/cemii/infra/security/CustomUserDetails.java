package com.divas.cemii.infra.security;

import com.divas.cemii.domain.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String senha;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String senha, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.authorities = authorities;
    }

    // ✅ Construtor auxiliar para criar direto a partir da entidade Usuario
    public static CustomUserDetails fromUsuario(Usuario usuario) {
        // Se futuramente quiser usar perfis ou papéis, pode mapear aqui
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                Collections.emptyList() // Sem roles por enquanto
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
