package org.wtaa.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.wtaa.dto.user;
import org.wtaa.service.AccountManager;
import org.wtaa.service.Encoder;
import org.wtaa.service.TokenExtractor;

import io.smallrye.jwt.build.Jwt;

@RequestScoped
@Path("/user")
public class UserAuthentication {

    @Inject
    Encoder encoder;

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenExtractor tokenExtractor;

    @Inject
    AccountManager accountManager;

    @PUT
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context SecurityContext ctx, user user) {
        var valid = accountManager.login(user.name(), user.password());

        if (valid == null)
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        else
            return Response
                    .ok(
                            Jwt
                                    .issuer("https://wtaa.org")
                                    .upn(valid.name())
                                    .groups(accountManager.getRoles(valid))
                                    .claim("Login Data", valid.content())
                                    .sign())
                    .build();
    }

    @GET
    @Path("/data")
    @RolesAllowed({ "admin", "user" })
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkToken(@Context SecurityContext ctx) {

        System.out.println("\tToken will be checked via TokenExtractor");

        var extractor = tokenExtractor
                .setToken(ctx, jwt);

        if (extractor.isValid()) {
            return Response.ok(
                    // extractor
                    // .exportClaim("Login Data")
                    // .getData()
                    jwt)
                    .build();
        } else {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }
    }

    @GET
    @Path("/token")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context SecurityContext ctx) {

        var encoded_token = encoder.decodeToken(jwt.getRawToken());

        return Response
                .ok(
                        Map.of("Content", encoded_token))
                .build();

    }

}