package com.divas.cemii.infra.security;

import com.divas.cemii.domain.model.Usuario;
import com.divas.cemii.domain.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = recuperarToken(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = tokenService.validateToken(token);

                if (email != null) {
                    Usuario usuario = usuarioRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                    // Define uma role padrão para todos os usuários
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                    var authentication =
                            new UsernamePasswordAuthenticationToken(usuario, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro no filtro de segurança: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}