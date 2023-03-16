package org.wtaa.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.wtaa.dao.user_dao;
import org.wtaa.dto.user;
import org.wtaa.persistance.context;

import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("/db")
public class db_test {

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbc;

    @Inject
    context ctx;

    @GET
    @Path("/test")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() throws Exception {
        try
        {
            Connection                  conn    = ctx.getContext(jdbc);
            user_dao                    dao     = new user_dao(conn);
            Set <org.wtaa.domain.user>  users   = dao.select();
            List<org.wtaa.dto.user>     data    = users.stream().map(user::of).toList();
                                        return
            Response                    .ok(data).build();
        }
        catch (Exception ex)
        {
            ex          .printStackTrace();
                        return 
            Response    .ok("test failed")
                        .build();
        }
    }

    @PUT
    @Path("/add")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(user usr) throws SQLException {
        try
        {
            Connection                  conn    = ctx.getContext(jdbc);
            user_dao                    dao     = new user_dao(conn);
            boolean                     success = dao.insert(usr);
            if (!success)               throw
                                        new Exception("insert failed");
            Set <org.wtaa.domain.user>  users   = dao.select();
            List<org.wtaa.dto.user>     data    = users.stream().map(user::of).toList();
                                        return
            Response                    .ok(data).build();
        }
        catch (Exception ex)
        {
            ex          .printStackTrace();
                        return 
            Response    .ok("test failed")
                        .build();
        }
    }
}