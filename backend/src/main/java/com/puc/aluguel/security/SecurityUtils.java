package com.puc.aluguel.security;

import com.puc.aluguel.model.Perfil;
import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** Extrai o Perfil a partir da authority ROLE_XXX. */
    public static Perfil perfil(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> Perfil.valueOf(a.substring(5)))
                .findFirst()
                .orElseThrow();
    }
}
