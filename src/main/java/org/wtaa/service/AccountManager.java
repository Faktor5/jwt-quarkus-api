package org.wtaa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class AccountManager {
    public Set<user> accounts;
    private Map<String, List<user>> role_mapping;
    public List<String> roles;

    @Inject
    private Encoder encoder;

    public AccountManager() {
        accounts = new HashSet<>();
        roles = List.of("admin", "user", "guest");
        role_mapping = new HashMap<>();

        role_mapping.put("admin", new ArrayList<user>());
        role_mapping.put("user", new ArrayList<user>());
        role_mapping.put("guest", new ArrayList<user>());

        initializeAccounts();
    }

    private void initializeAccounts() {
        accounts.add(new user("admin", (hashed("admin"))));
        accounts.add(new user("amin", (hashed("amin"))));
        accounts.add(new user("nima", (hashed("nima"))));
        accounts.add(new user("anon", (hashed("anon"))));

        role_mapping.get("admin").add(accounts.stream().filter(u -> u.name().equals("admin")).findFirst().get());
        role_mapping.get("admin").add(accounts.stream().filter(u -> u.name().equals("amin")).findFirst().get());

        role_mapping.get("user").add(accounts.stream().filter(u -> u.name().equals("nima")).findFirst().get());

        role_mapping.get("guest").add(accounts.stream().filter(u -> u.name().equals("anon")).findFirst().get());
    }

    private String hashed(String in) {
        return encoder.encode(in);
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
}
