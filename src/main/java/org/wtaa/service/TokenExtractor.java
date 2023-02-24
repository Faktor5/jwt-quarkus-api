package org.wtaa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.wtaa.model.token;

@RequestScoped
public class TokenExtractor {
    // #region Fields
    private boolean valid;
    private String name;
    private boolean isSecure;
    private String scheme;
    private boolean isJwt;

    private List<String> roles;
    private Map<String, String> claims;

    // #region Temp fields
    private JsonWebToken jwt;
    // #endregion

    // #endregion

    public TokenExtractor() {
        valid = false;
        name = "anonymous";
        isSecure = false;
        scheme = "none";
        isJwt = false;

        roles = List.of();
        claims = new HashMap<>();

        System.out.println("TokenExtractor created");
    }

    // #region Pipeline
    public TokenExtractor setToken(SecurityContext ctx, JsonWebToken jwt) {
        try {
            this.jwt = jwt;

            System.out.println("TokenExtractor setToken");

            if (jwt == null)
                throw new Exception("JWT is null");

            if (ctx == null)
                throw new Exception("SecurityContext is null");
            if (ctx.getUserPrincipal() == null)
                name = "anonymous";
            else if (!ctx.getUserPrincipal()
                    .getName().equals(
                            jwt.getName()))
                throw new Exception("Principal name is not equal to JWT name");
            else
                name = ctx.getUserPrincipal().getName();

            isSecure = ctx.isSecure();
            scheme = ctx.getAuthenticationScheme();
            isJwt = jwt.getClaimNames() != null;

            roles = jwt.getGroups().stream().toList();

            valid = true;

            return this;

        } catch (Exception e) {

            System.out.println("TokenExtractor setToken error: ");

            valid = false;
            return this;

        }
    }

    public TokenExtractor exportClaim(String claim) {

        System.out.println("TokenExtractor exportClaims");

        if (!valid)
            return this;
        
        if (jwt.getClaim(claim) == null)
            return this;

        claims.put(claim, jwt.getClaim(claim));
        return this;
    }

    public TokenExtractor exportAllClaims() {
            
        System.out.println("TokenExtractor exportAllClaims");

        if (!valid)
            return this;

        System.out.println("TokenExtractor exportAllClaims: " + jwt.getClaimNames());

        for (String claim : jwt.getClaimNames()) {
            var value = jwt.getClaim(claim);
            
            if (value == null)
                continue;

            if (claim.equals("raw_token"))
                continue;
            
            if (claim.equals("upn"))
                continue;

            // if (claim.equals("iss"))
            //     continue;
            
            // if (claim.equals("iat"))
            //     continue;
            
            // if (claim.equals("exp"))
            //     continue;
            
            if (claim.equals("jti"))
                continue;

            if(value instanceof String)
                claims.put(claim, (String) value);
            else if(value instanceof Integer)
                claims.put(claim, String.valueOf((Integer) value));
            else if(value instanceof Long)
                claims.put(claim, String.valueOf((Long) value));
            else if(value instanceof Double)
                claims.put(claim, String.valueOf((Double) value));
            else if(value instanceof Float)
                claims.put(claim, String.valueOf((Float) value));
            else if(value instanceof Boolean)
                claims.put(claim, String.valueOf((Boolean) value));
            
        }

        return this;
    }
    // #endregion

    // #region Final
    public boolean isValid() {

        System.out.println("TokenExtractor isValid");

        return valid;
    }

    public token getData() {

        System.out.println("TokenExtractor getData");

        return new token(valid, name, isSecure, scheme, isJwt, roles, claims);
    }

    public List<String> getRoles() {
            
        System.out.println("TokenExtractor getRoles");

        return roles;
    }

    public String getName() {
                
        System.out.println("TokenExtractor getName");

        return name;
    }

    public Optional<String> getClaim(String claim) {
                
        System.out.println("TokenExtractor getClaim");

        return Optional.of(claims)
            .map(claims -> claims.get(claim));
    }
    // #endregion

}
