package org.wtaa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.wtaa.domain.*;

public class user_dao {

    private Connection conn;

    public user_dao(Connection conn) {
        this.conn = conn;
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
            System.out.println(e.getMessage());
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
}
