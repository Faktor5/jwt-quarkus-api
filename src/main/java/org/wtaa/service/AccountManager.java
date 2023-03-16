package org.wtaa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.wtaa.dto.user;

@ApplicationScoped
public class AccountManager {
    public Set<user> accounts;
    private Map<String, List<user>> role_mapping;

    Encoder encoder;

    public AccountManager(Encoder encoder) {
        
        this.encoder = encoder;

        accounts = createUser(
        "admin",
            "amin",
            "nima",
            "anon");
        
        role_mapping = createRoles(
            "admin",
            "user",
            "guest");
    }

    private HashMap<String, List<user>> createRoles(String... roles) {
        return List.of(roles)
                .stream()
                .collect(HashMap::new, (map, role) -> map.put(role, getUserPerRole(role)), HashMap::putAll);
    }

    private List<user> getUserPerRole(String role) {
        return getUserNamesPerRole(role)
                .stream()
                .map(this::getUserByName)
                .filter(this::userIsNotNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private List<String> getUserNamesPerRole(String role) {
        return switch (role) {
            case "admin" -> List.of("admin", "amin");
            case "user" -> List.of("nima");
            case "guest" -> List.of("anon");
            default -> List.of();
        };
    }

    private Set<user> createUser(String... users) {
        return List.of(users)
                .stream()
                .map(this::createUserFromName)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    private user createUserFromName(String name) {
        return 
            new user(
                name,
                hashed(name)
            );
    }

    private String hashed(String in) {
        return
        Optional.of(encoder)
                .orElseGet(Encoder::new)
                .encodeBase64(in);
    }

    public user login(String user, String pass) {
        return accounts
                .stream()
                .filter(u -> u.name().equals(user) && u.password().equals(hashed(pass)))
                .findFirst()
                .orElse(null);
    }

    public Set<String> getRoles(user valid) {
        return role_mapping
                .entrySet()
                .stream()
                .filter(e -> e.getValue().contains(valid))
                .map(Map.Entry::getKey)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    private boolean userIsNotNull(user u) {
        return u != null;
    }

    private user getUserByName(String name) {
        return accounts
                .stream()
                .filter(u -> u.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
