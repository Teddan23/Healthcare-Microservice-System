package com.example.FullstackFhirService.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleConverter implements org.springframework.core.convert.converter.Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        System.out.println("In convert method");

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        return new JwtAuthenticationToken(jwt, authorities, getPrincipleClaimName(jwt));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        System.out.println("in extract method");
        Collection<GrantedAuthority> authorities = Optional.ofNullable(jwt.getClaim("realm_access"))
                .map(realmAccess -> (Map<String, Object>) realmAccess)
                .map(realmAccess -> (Collection<String>) realmAccess.get("roles"))
                .orElse(List.of())
                .stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        System.out.println("Extracted Authorities: " + authorities);

        return authorities;
    }

    private String getPrincipleClaimName(Jwt jwt) {
        return jwt.getClaim("preferred_username");
    }
}
