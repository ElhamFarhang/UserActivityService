package com.example.useractivityservice.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;



@Component
public class UserInfo {

/*
    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Jwt getJwt() {
        Authentication auth = getAuth();
        if (auth != null && auth.getPrincipal() instanceof Jwt) {
            return (Jwt) auth.getPrincipal();
        }
        throw new IllegalStateException("No JWT token found in SecurityContext");
    }*/

    public String getUserId() {
        return "1b2d25d9-0ac1-494d-8208-6b15b6430f3a";
//        return getJwt().getSubject();
    }


    public String getRole(){
/*        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("NO_ROLE");*/
        return "user";
    }

}
