package org.wtaa.dto;

public record user(String name, String password) {
    public String content(){
        return "name: " + name + " password: " + password;
    }

    public static user of(org.wtaa.domain.user user) {
        return new user(user.name(), user.pass());
    }
}
