package com.tomato.naraclub.admin.user.code;

import java.util.Arrays;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author : MinjaeKim
 * @packageName : com.tomato.naraclub.admin.user.code
 * @fileName : AdminRole
 * @date : 2025-04-28
 * @description :
 * @AUTHOR : MinjaeKim
 */
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
