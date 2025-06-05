package com.aventura.api.security;

import com.aventura.api.service.UsuarioService;
import com.aventura.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    @Lazy
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🟡 JwtAuthenticationFilter ejecutado");

        String token = getJwtFromRequest(request);
        System.out.println("🔐 TOKEN recibido: " + token);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            String userId = tokenProvider.getUserIdFromToken(token);
            System.out.println("✅ JWT válido. Subject (UUID): " + userId);

            try {
                Usuario usuario = usuarioService.findById(UUID.fromString(userId)).orElse(null);
                if (usuario != null) {
                    System.out.println("🙋 Usuario autenticado: " + usuario.getEmail());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            usuario, null, null
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("⚠️ Usuario con ID no encontrado en DB: " + userId);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Token inválido: no es UUID válido -> " + userId);
            }
        } else {
            System.out.println("🚫 Token no presente o no válido");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
