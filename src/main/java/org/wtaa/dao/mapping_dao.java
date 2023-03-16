package org.wtaa.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.wtaa.domain.*;

public class mapping_dao {

    private Connection conn;
    private user_dao user_dao;
    private role_dao role_dao;

    public mapping_dao(Connection conn, user_dao user_dao, role_dao role_dao) {
        this.conn = conn;

        this.user_dao = user_dao;
        this.role_dao = role_dao;
    }

    public Set<String> user_roles(String name) {

        var id = user_dao
                .name(name)
                .map(u -> u.id())
                .orElseThrow();

        var roles = roles(id)
                .stream()
                .map(role_dao::id)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(role::role)
                .collect(Collectors.toSet());

        return roles;
    }

    public List<Integer> roles(int user_id) {
        String query = "SELECT role_id FROM role_mapping WHERE user_id = " + user_id;
        List<Integer> roles = new ArrayList<>();

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next())
                roles.add(rs.getInt("role_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    public boolean add_combi(int user_id, int role_id) {
        String query = "INSERT INTO role_mapping (user_id, role_id) VALUES (" + user_id + ", " + role_id + ")";
        boolean result = false;

        try (
                Statement stmt = conn.createStatement();) {
            result = stmt.executeUpdate(query) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}