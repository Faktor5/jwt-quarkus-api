package org.wtaa.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.wtaa.model.user;

import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("/db")
public class db_test {

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbc;

    @GET
    @Path("/test")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() throws SQLException {
        String sql = "SELECT id, name, pass FROM user";
      
        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            

            // loop through the result set
            var sb = new StringBuilder();
            while (rs.next()) {
                sb.append(
                    rs.getInt("id") +  "\t" + 
                    rs.getString("name") + "\t" +
                    rs.getDouble("pass") + " ");
                sb.append("\r\n");
            }
            return Response.ok(sb.toString()).build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Response.ok("test failed").build();
    }

    @PUT
    @Path("/add")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(user usr) throws SQLException {
        int id = 0;
        String sql1 = "SELECT MAX(id) FROM user";
        
        try (Connection conn = this.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql1)){
            
            // loop through the result set
            while (rs.next()) {
                id = rs.getInt("MAX(id)");
            }
            String sql2 = "INSERT INTO user(id, name, pass) VALUES(" + (id + 1) + ", '" + usr.name() + "', '" + usr.password() + "')";
            stmt.executeUpdate(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (!(id > 0)) {
            return Response.ok("test failed").build();
        }

        try (Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery("SELECT id, name, pass FROM user")){
            
            // loop through the result set
            var sb = new StringBuilder();
            while (rs.next()) {
                sb.append(
                    rs.getInt("id") +  "\t" + 
                    rs.getString("name") + "\t" +
                    rs.getDouble("pass") + " ");
                sb.append("\r\n");
                
            }
            return Response.ok(sb.toString()).build();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        

        return Response.ok("test failed completly").build();
    }

    private Connection connect() throws SQLException {
        Connection conn;
        // db parameters
        String url = jdbc;
        // create a connection to the database
        conn = DriverManager.getConnection(url);
        return conn;
    }
}
