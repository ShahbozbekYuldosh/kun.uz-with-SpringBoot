package dasturlash.uz.util;

import dasturlash.uz.dto.JwtDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.security.KeyRep.Type.SECRET;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // TOKEN YARATISH
    public String generateToken(JwtDTO jwtDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", jwtDTO.getUsername());
        claims.put("role", jwtDTO.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(jwtDTO.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))
                .signWith(secretKey)
                .compact();
    }

    public String generateEmailVerificationToken(Integer userId, String username) {
        return Jwts.builder()
                .claim("id", userId)
                .claim("username", username)
                .claim("type", "verify")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 soat
                .signWith(secretKey)
                .compact();
    }

    public String generateAccessToken(Integer id, String username, String role) {
        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 soat
                .signWith(secretKey)
                .compact();
    }

    // TOKEN ICHIDAN USERNAME OLIB BERISH
    public String extractUsername(String token) {
        return getAllClaims(token).get("username", String.class);
    }

    // TOKEN ICHIDAN ROLE OLIB BERISH
    public String extractRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    // HAMMA CLAIMS
    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // TOKEN TEKSHIRISH
    public boolean validateToken(String token, JwtDTO dto) {
        return extractUsername(token).equals(dto.getUsername());
    }
}