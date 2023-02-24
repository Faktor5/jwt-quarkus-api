package org.wtaa.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.microprofile.jwt.JsonWebToken;

import org.wtaa.service.TokenExtractor;

import io.smallrye.jwt.build.Jwt;

@RequestScoped
@Path("/auth")
public class Authorization {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenExtractor tokenExtractor;

    @GET
    @Path("/token")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkToken(@Context SecurityContext ctx) {

        System.out.println("\tToken will be checked via TokenExtractor");
        var extractor = tokenExtractor.setToken(ctx, jwt);

        if (extractor.isValid()) {
            return Response.ok(
                    extractor
                            // .exportClaim("custom")
                            // .exportClaim("custom2")
                            // .exportClaim("anonym")
                            .exportAllClaims()
                            .getData())
                    .build();
        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @POST
    @Path("/get-user-via-name")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleWithName(@Context SecurityContext ctx, String name) {

        System.out.println("Authenticating user: " + name + " ...");

        String token = Jwt
                .issuer("https://wtaa.org")
                .upn(name)
                .groups(new HashSet<>(Arrays.asList("user")))
                .claim("anonym", "true")
                .sign();

        return Response.ok(token).build();
    }

    @GET
    @Path("/get-role")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRole(@Context SecurityContext ctx) {
        String token = Jwt
                .issuer("https://wtaa.org")
                .upn("Nima")
                .groups(new HashSet<>(Arrays.asList("admin", "user")))
                .claim("custom", "custom")
                .claim("custom2", "custom2")
                .sign();

        return Response.ok(token).build();
    }

    @GET
    @Path("/get-weak-role")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeakRole(@Context SecurityContext ctx) {
        String token = Jwt
                .issuer("https://wtaa.org")
                .upn("Nima")
                .groups(new HashSet<>(Arrays.asList("user")))
                .claim("custom", "custom")
                .claim("custom2", "custom2")
                .sign();

        return Response.ok(token).build();
    }

    @GET
    @Path("/check-admin-rights")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasAdminAuthority(@Context SecurityContext ctx) {

        System.out.println("\tToken will be checked via TokenExtractor");

        var extractor = tokenExtractor.setToken(ctx, jwt);

        if (extractor.isValid()) {

            var results = Map.of(
                    "Name", extractor.getName(),
                    "Roles", extractor.getRoles());

            return Response
                    .ok(results)
                    .build();
        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @GET
    @Path("/check-rights")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleAuthority(@Context SecurityContext ctx) {

        System.out.println("\tToken will be checked via TokenExtractor");

        var extractor = tokenExtractor.setToken(ctx, jwt);

        if (extractor.isValid()) {

            var results = Map.of(
                    "Name", extractor.getName(),
                    "Roles", extractor.getRoles());

            return Response
                    .ok(results)
                    .build();

        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @GET
    @Path("/get-nima-infos")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNimaInfos(@Context SecurityContext ctx) {

        System.out.println("\tToken will be checked via TokenExtractor");

        var extractor = tokenExtractor.setToken(ctx, jwt);

        if (extractor.isValid()) {

            var results = Map.of(
                    "Name", extractor.getName(),
                    "Roles", extractor.getRoles());

            if (results.get("Name").equals("Nima")) {
                return Response
                        .ok("You have access to Nima's infos; \r\n"
                                + String.format("And your roles are: %s", results.get("Roles")))
                        .build();
            } else {
                return Response
                        .ok("You don't have access to Nima's infos")
                        .build();
            }

        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }
}
