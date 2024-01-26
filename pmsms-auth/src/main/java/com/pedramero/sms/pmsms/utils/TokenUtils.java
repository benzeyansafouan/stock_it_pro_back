package com.pedramero.sms.pmsms.utils;

import static com.pedramero.sms.pmsms.utils.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.pedramero.sms.pmsms.utils.Constants.SIGNING_KEY;

import com.pedramero.sms.pmsms.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils implements Serializable {

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(SIGNING_KEY)
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        return doGenerateToken(user);
    }

    private String doGenerateToken(User subject) {
        Claims claims = Jwts.claims().setSubject(subject.getUsername());
        claims.put("role", subject.getRole().name());
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer("pedra_mero")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
            .compact();
    }

    public Boolean validateToken(String token, User user) {
        final String username = getUsernameFromToken(token);
        return (
            username.equals(user.getUsername())
                && !isTokenExpired(token));
    }

}
