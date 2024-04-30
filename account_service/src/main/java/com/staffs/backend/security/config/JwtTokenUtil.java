package com.staffs.backend.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 7200; // 2 hours
    private static final long REFRESH_EXPIRATION_TIME = 900_000;

    @Value("${jwt.secret:vjknknidicgvgvjhbhjkdbskjdjfbjfdjbdjfdshfdsjfdskjhfdskfdhkhfnddfhfkdhfkdhvghindi}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token , Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token , Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token , Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSigningKey()));
    }

    private String getSigningKey() {
        return Encoders.BASE64.encode(secret.getBytes());
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String username , String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType" , userType);
        return doGenerateToken(claims , username);
    }

    private String doGenerateToken(Map<String, Object> claims , String subject) {
        return Jwts.builder().claims().add(claims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).and().signWith(getSecretKey()).compact();
    }

    //updated to capture admin users
    public boolean validateToken(String token , UserDetails userDetails) {
        final String username = getUsernameFromToken(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Refresh JWT token
    public String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        Date expiration = claims.getExpiration();
        Date now = new Date();

        // Check if token needs refreshing (e.g., within the last 30 minutes of expiration)
        if (expiration.getTime() - now.getTime() <= REFRESH_EXPIRATION_TIME) {
            return doGenerateToken(claims , claims.getSubject());
        }

        return token; // Return the original token if no refresh is needed
    }

}