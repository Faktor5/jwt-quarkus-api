package org.wtaa.model;

import java.util.List;
import java.util.Map;

public record token(

    boolean valid,
    String name,
    boolean isSecure,
    String scheme,
    boolean isJwt,
    
    List<String> roles,
    Map<String, String> claims
    
) { }