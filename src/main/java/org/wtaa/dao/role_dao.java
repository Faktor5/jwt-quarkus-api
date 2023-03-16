package org.wtaa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.wtaa.domain.*;

public class role_dao {
    
    private Connection conn;

    public role_dao(Connection conn) {
        this.conn = conn;
    }

    public List<String> roles() {
        String query = "SELECT role FROM role";
        List<String> roles = new ArrayList<>();

        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next())
                roles.add(rs.getString("role"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    public Set<role> select() {
        String query = "SELECT id, role FROM role";
        Set<role> roles = new HashSet<>();

        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next())
                roles.add(
                    new role(
                        rs.getInt("id"),
                        rs.getString("role")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    public boolean insert(String inserted) {
        String query = String.format(
            "INSERT INTO role (role) VALUES ('%s')",
            inserted);

        boolean result = false;

        try (
            Statement stmt = conn.createStatement();
        ) {
            result =
            stmt.executeUpdate(query) == 1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Optional<role> id(int id) {
        return select()
                .stream()
                .filter(r -> r.id() == id)
                .findAny();
    }

    public Optional<Integer> name(String name) {
        return select()
                .stream()
                .filter(r -> r.role().equals(name))
                .map(r -> r.id())
                .findAny();
    } 
}
