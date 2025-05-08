package com.tomato.naraclub.admin.user.code;

import java.util.Arrays;
import org.springframework.security.core.GrantedAuthority;


public enum AdminRole implements GrantedAuthority {
    ADMIN
    ;

    public static AdminRole getAdminRole(String role) {
        return Arrays.stream(values())
            .filter(adminRole -> adminRole.name().equals(role))
            .findAny()
            .orElse(null);
    }


    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
