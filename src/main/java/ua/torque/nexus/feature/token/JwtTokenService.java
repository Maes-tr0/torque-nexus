package ua.torque.nexus.feature.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.emailconfirmation.exception.TokenExpiredException;
import ua.torque.nexus.feature.emailconfirmation.exception.TokenNotFoundException;
import ua.torque.nexus.user.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    @Value("${jwt.confirmation.secret}")
    private String confirmationSecret;

    @Value("${jwt.confirmation.expiration}")
    private long confirmationExpirationMs;


    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(confirmationSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateConfirmationToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + confirmationExpirationMs);

        String token = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("email", user.getEmail())
                .signWith(getKey())
                .compact();

        log.info("Generated confirmation token for user {}: {}", user.getEmail(), token);
        return token;
    }


    public Claims validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new TokenNotFoundException("Token not provided");
        }
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", token, e);
            throw new TokenExpiredException("Token expired", Map.of("expiredAt", e.getClaims().getExpiration()));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", token, e);
            throw new TokenNotFoundException("Invalid JWT token");
        }
    }
}
