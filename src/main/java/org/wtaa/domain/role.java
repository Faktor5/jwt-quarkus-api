package org.wtaa.domain;

public record role(
    int id, 
    String role) {
    
    public boolean equals(role other) {
        return this.id == other.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof role)) {
            return false;
        }
        role other = (role) obj;
        return this.id == other.id;
    }
}
