package org.wtaa.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;


@RequestScoped
public class context {
    public Connection getContext(String jdbc) throws SQLException {
        return connect(jdbc);
    }

    private Connection connect(String jdbc) throws SQLException {
        return Optional.ofNullable(jdbc)
                .map(this::getConnection)
                .orElseThrow(() -> new SQLException("Connection failed"));
    }

    private Connection getConnection(String string1) {
        try {
            return DriverManager
                .getConnection(string1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
