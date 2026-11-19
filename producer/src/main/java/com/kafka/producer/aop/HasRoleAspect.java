package com.kafka.producer.aop;

import com.kafka.producer.annotation.HasRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class HasRoleAspect {

    @Around("@annotation(hasRole)")
    public Object checkUserRole(
            ProceedingJoinPoint joinPoint,
            HasRole hasRole) throws Throwable {

        // Get the authenticated user from Spring Security
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Check whether the user is authenticated
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            throw new AuthenticationCredentialsNotFoundException(
                    "User is not authenticated"
            );
        }

        // Roles required by the API method
        String[] requiredRoles = hasRole.value();

        // Roles assigned to the authenticated user
        Set<String> userRoles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Allow access if at least one required role matches
        boolean hasAccess = Arrays.stream(requiredRoles)
                .anyMatch(userRoles::contains);

        if (!hasAccess) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You do not have permission to access this API"
            );
        }

        // Authorization successful: execute the API method
        return joinPoint.proceed();
    }
}
