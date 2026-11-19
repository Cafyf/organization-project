package com.kafka.producer.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final String JWT_HEADER = "Jwt-token";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(JWT_HEADER);

        // No token provided: continue.
        // Protected endpoints will reject unauthenticated requests.
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Step 1: Validate JWT and extract claims.
            Claims claims = jwtService.validateAndExtractClaims(token);

            // Step 2: Extract username.
            String username = jwtService.extractUsername(claims);

            // Step 3: Extract roles.
            List<String> roles = jwtService.extractRoles(claims);

            if (username == null || username.isBlank() || roles == null) {
                throw new IllegalArgumentException("Required JWT claims are missing");
            }

            // Step 4: Convert roles into GrantedAuthority.
            List<SimpleGrantedAuthority> authorities =
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            // Step 5: Create UserDetails.
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    username,
                    authorities
            );

            // Step 6: Create Authentication object.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );

            // Step 7: Store authenticated user.
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException exception) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token"
            );

            return;
        }

        // Step 8: Continue filter chain.
        filterChain.doFilter(request, response);
    }
}
