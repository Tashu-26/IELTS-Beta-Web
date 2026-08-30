package com.ieltsbeta.security;

import com.ieltsbeta.repository.AppUserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SupabaseJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AppUserRepository appUserRepository;

    public SupabaseJwtAuthenticationConverter(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID authUserId = UUID.fromString(jwt.getSubject());

        List<GrantedAuthority> authorities = appUserRepository.findByAuthUserId(authUserId)
                .<List<GrantedAuthority>>map(user -> {
                    if (!Boolean.TRUE.equals(user.getIsActive())) {
                        // Suspended: still authenticated (so GET /api/me works and
                        // shows their status), but zero role authorities means every
                        // @PreAuthorize-protected endpoint rejects them with 403.
                        return List.of();
                    }
                    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
                })
                // No matching row yet (e.g. first request right after Supabase signup,
                // before /api/auth/complete-profile has run) -> authenticated, no role yet.
                .orElse(List.of());

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
