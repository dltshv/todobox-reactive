package io.dltshv.todobox.jwt;

import java.security.Principal;

public class JwtPrincipal implements Principal {

    private final String name;

    public JwtPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
