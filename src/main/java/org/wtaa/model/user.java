package org.wtaa.model;

public record user(String name, String password) {
    public String content(){
        return "name: " + name + " password: " + password;
    }
}
