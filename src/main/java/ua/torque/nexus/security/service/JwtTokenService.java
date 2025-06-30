package ua.torque.nexus.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.common.exception.AuthenticationException;
import ua.torque.nexus.user.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtTokenService {

    @Value("${jwt.auth.secret}")
    private String authSecret;
    @Value("${jwt.auth.expiration-ms}")
    private long authExpirationMs;

    @Value("${jwt.confirmation.secret}")
    private String confirmationSecret;
    @Value("${jwt.confirmation.expiration-ms}")
    private long confirmationExpirationMs;


    public String generateAuthorizationToken(User user) {
        log.debug("Generating authorization token for user '{}'", user.getEmail());
        Map<String, Object> claims = Map.of("role", user.getRole().getType().name());
        return buildToken(claims, user.getEmail(), authExpirationMs, getSignInKey(authSecret));
    }

    public String generateConfirmationToken(User user) {
        log.debug("Generating confirmation token for user '{}'", user.getEmail());
        return buildToken(Map.of(), user.getEmail(), confirmationExpirationMs, getSignInKey(confirmationSecret));
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, getSignInKey(authSecret));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, getSignInKey(authSecret));
    }

    public Claims validateConfirmationToken(String token) {
        return extractAllClaims(token, getSignInKey(confirmationSecret));
    }


    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration, SecretKey key) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    private boolean isTokenExpired(String token, SecretKey key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("Invalid JWT token provided: {}", e.getMessage());
            throw new AuthenticationException(ExceptionType.TOKEN_INVALID, e.getMessage());
        }
    }

    private SecretKey getSignInKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}