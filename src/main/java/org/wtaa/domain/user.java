package org.wtaa.domain;

public record user(
    int id,
    String name,
    String pass
) {
    public boolean equals(user other) {
        return this.id == other.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof user)) {
            return false;
        }
        user other = (user) obj;
        return this.id == other.id;
    }
}
