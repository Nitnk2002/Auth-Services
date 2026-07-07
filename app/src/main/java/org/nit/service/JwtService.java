package org.nit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET = "cef0a42aeab48d424a82c39bd6ddb0b8788e3ef68a6fbabde2561e57c8d28d7e";

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply (claims);
    }

    public Date extractExpiration(String token){
        return extractClaim (token, Claims::getExpiration);
    }

    private Boolean isTokeExpired(String token){
        return extractExpiration (token).before (new Date ());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername (token);
        return (username.equals (userDetails.getUsername ()) && !isTokeExpired (token));
    }

    public String GenerateToken(String username){
        Map<String, Object> claims = new HashMap<> ();
        return createToken (claims, username);
    }
    private String createToken(Map<String , Object> claims, String username){

        return Jwts.builder ()
                .setClaims (claims)
                .setIssuedAt (new Date (System.currentTimeMillis ()))
                .setExpiration (new Date (System.currentTimeMillis () + 1000 * 60 * 60))
                .signWith (getSignKey (), SignatureAlgorithm.HS256).compact ();
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder ()
                .setSigningKey (getSignKey())
                .build ()
                .parseClaimsJws (token)
                .getBody ();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode (SECRET);
        return Keys.hmacShaKeyFor (keyBytes);
    }
}
