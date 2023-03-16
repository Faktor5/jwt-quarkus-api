package org.wtaa.model;

import java.util.ArrayList;
import java.util.List;

public record user(
    String name,
    List<org.wtaa.domain.role> roles
) {
    public user {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name is required");
        if (roles == null)
            roles = new ArrayList<>();
    }

    public static user of(
        org.wtaa.domain.user user,
        List<org.wtaa.domain.role> roles) {
        return new user(user.name(), roles);
    }

    public static user of(org.wtaa.domain.user user) {
        return new user(user.name(), new ArrayList<>());
    }
}