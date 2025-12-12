package dasturlash.uz.util;

import dasturlash.uz.dto.JwtDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    // ---------------- GENERATE TOKENS ----------------
    public String generateToken(JwtDTO jwtDTO) {
        Map<String, Object> claims = new HashMap<>();
        String cleanRole = jwtDTO.getRole().replace("ROLE_", "");
        claims.put("username", jwtDTO.getUsername());
        claims.put("role", cleanRole);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(jwtDTO.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 soat
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(Integer id, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        String cleanRole = role.replace("ROLE_", "");
        claims.put("id", id);
        claims.put("username", username);
        claims.put("role", cleanRole);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 soat
                .signWith(secretKey)
                .compact();
    }

    public String generateEmailVerificationToken(Integer userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);
        claims.put("type", "verify");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 soat
                .signWith(secretKey)
                .compact();
    }

    // ---------------- EXTRACT DATA ----------------
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            throw new RuntimeException("JWT noto‘g‘ri: " + ex.getMessage());
        }
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Integer extractUserId(String token) {
        Object idObj = extractAllClaims(token).get("id");
        return idObj instanceof Integer ? (Integer) idObj : ((Number) idObj).intValue();
    }

    // ---------------- VALIDATE ----------------
    public boolean validateToken(String token, JwtDTO dto) {
        try {
            final String username = extractUsername(token);
            return username.equals(dto.getUsername());
        } catch (Exception e) {
            return false;
        }
    }
}
