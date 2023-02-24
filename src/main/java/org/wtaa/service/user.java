package org.wtaa.service;

public record user(String name, String password) {
    public String content(){
        return "name: " + name + " password: " + password;
    }
}
