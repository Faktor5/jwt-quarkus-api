package org.wtaa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.wtaa.domain.*;

public class user_dao {

    private Connection conn;

    public user_dao(Connection conn) {
        this.conn = conn;
    }

    public List<String> names() {
        String query = "SELECT name FROM user";
        List<String> names = new ArrayList<>();

        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next())
                names.add(rs.getString("name"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return names;
    }

    public Set<user> select() {
        String query = "SELECT id, name, pass FROM user";
        Set<user> users = new HashSet<>();

        try (
            Statement stmt = conn.createStatement();
            ResultSet rs   = stmt.executeQuery(query)
        ) {
            while (rs.next())
                users.add(
                    new user(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("pass")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean insert(org.wtaa.dto.user inserted) {
        String query = String.format(
            "INSERT INTO user(name, pass) VALUES('%s', '%s')",
            inserted.name(), inserted.password());

        boolean result = false;
            
        try (Statement stmt = conn.createStatement())
        {
            result =
            stmt.executeUpdate(query) == 1;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Optional<user> get(org.wtaa.dto.user usr) {
        return select()
                .stream()
                .filter(u ->
                    u.name().equals(usr.name()) &&
                    u.pass().equals(usr.password()))
                .findAny();
    }

    public Optional<user> id(int id) {
        return select()
                .stream()
                .filter(u -> u.id() == id)
                .findAny();
    }

    public Optional<user> name(String name) {
        return select()
                .stream()
                .filter(u -> u.name().equals(name))
                .findAny();
    }
}
