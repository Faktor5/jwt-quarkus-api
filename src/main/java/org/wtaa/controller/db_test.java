package org.wtaa.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.wtaa.dao.mapping_dao;
import org.wtaa.dao.role_dao;
import org.wtaa.dao.user_dao;
import org.wtaa.dto.user;
import org.wtaa.persistance.context;

import io.smallrye.jwt.build.Jwt;

import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("/db")
public class db_test {

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbc;

    @Inject
    context ctx;

    @GET
    @Path("/secret")
    @RolesAllowed({ "admin" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() throws Exception {
        try {
            Connection conn = ctx.getContext(jdbc);
            user_dao dao = new user_dao(conn);
            Set<org.wtaa.domain.user> users = dao.select();
            List<org.wtaa.dto.user> data = users.stream().map(user::of).toList();
            return Response.ok(data).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ok("test failed")
                    .build();
        }
    }

    @GET
    @Path("/inofficial")
    @RolesAllowed({ "qualified" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response ressourcing() throws SQLException {
        try {
            Connection conn = ctx.getContext(jdbc);
            user_dao dao = new user_dao(conn);
            List<String> names = dao.names();

            return Response
                .ok(names)
                .build();

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ok("test failed")
                    .build();
        }
    }

    @GET
    @Path("/official")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response open_for_all() throws SQLException {
        try {
            Connection conn = ctx.getContext(jdbc);
            user_dao dao = new user_dao(conn);
            List<String> names = dao.names();

            return Response
                .ok(names.size())
                .build();

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.ok("test failed")
                    .build();
        }
    }

    @GET
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(org.wtaa.dto.user login_data) throws Exception {
        try {
            Connection conn = ctx.getContext(jdbc);
            user_dao dao = new user_dao(conn);
            role_dao role_dao = new role_dao(conn);
            mapping_dao mapper = new mapping_dao(conn, dao, role_dao);
            List<String> names = dao.names();
            Optional<org.wtaa.domain.user> user = dao.get(login_data);

            if(user.isEmpty())
                if (names.contains(login_data.name()))
                     return Response
                        .ok("Wrong password")
                        .build();
                else return Response
                        .ok("User not found")
                        .build();
            
            else return Response
                .ok(
                    Jwt
                    .issuer("https://wtaa.org")
                    .upn(user.get().name())
                    .claim("User", user.get().name())
                    .groups(mapper.user_roles(user.get().name()))
                    .sign())
                .build();

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @PUT
    @Path("/register")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(org.wtaa.dto.user register_data) throws Exception {
        try {
            Connection conn     = ctx.getContext(jdbc);
            user_dao user_dao   = new user_dao(conn);
            role_dao role_dao   = new role_dao(conn);
            mapping_dao mapper  = new mapping_dao(conn, user_dao, role_dao);
            List<String> names  = user_dao.names();
            boolean success     = false;
            
            if(names.contains(register_data.name()))
                return Response
                    .ok("User already exists")
                    .build();
            else
                success = user_dao.insert(register_data);
            
            if (!success)
                throw new Exception("creating user failed");
            else 
                success = mapper.add_combi(
                    user_dao.name(register_data.name()).get().id(),
                    role_dao.name("qualified").get());

            if (!success)
                throw new Exception("setting role failed");
            else
                return Response
                    .ok(String.format("User %s was registered sucessfully",register_data.name()))
                    .build();
        
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}