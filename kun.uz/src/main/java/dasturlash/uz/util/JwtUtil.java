package dasturlash.uz.util;

import dasturlash.uz.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key:mySecretKeyForEmailVerificationThatIsVeryLongAndSecure123456789}")
    private String secretKey;

    @Value("${jwt.email.verification.expiration:900000}") // 15 minutes in milliseconds
    private Long emailVerificationExpiration;

    @Value("${jwt.access_token.expiration:86400000}")
    private long accessTokenExpiration;

    private Key key;

    @PostConstruct // Bu metod JWTUtil obyektini yaratgandan so'ng avtomatik ishlaydi
    public void init() {
        if (secretKey == null || secretKey.isEmpty() || secretKey.length() < 64) {
            // Agar Secret String kiritilmagan yoki juda qisqa bo'lsa, xavfsiz yangi kalit yaratish
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            // NOTE: Agar har safar ilova ishga tushganda yangi kalit yaraqlansa,
            // eski tokenlar yaroqsiz bo'ladi!
            System.out.println("Xavfsiz yangi kalit generatsiya qilindi!");

        } else {
            // Mavjud kalitni byte arrayga o'tkazib, xavfsiz qilish (Base64 yoki shunchaki Bytes)
            // Eng xavfsiz yo'li: kalitni Base64-encoded String qilib saqlash
            this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        }
    }

    // Secret key yaratish
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Email verification uchun JWT token yaratish
    public String generateEmailVerificationToken(Integer profileId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("profileId", profileId);
        claims.put("type", "EMAIL_VERIFICATION");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + emailVerificationExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token dan claims olish
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Token validate qilish
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Username olish
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Profile ID olish
    public Integer extractProfileId(String token) {
        return extractClaims(token).get("profileId", Integer.class);
    }

    public String generateAccessToken(Integer id, String username, ProfileRole role) {
        // Tokenning amal qilish muddatini hisoblash
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + accessTokenExpiration);

        // JWT payload (Claims) ichiga kiritiladigan qo'shimcha ma'lumotlar
        Map<String, Object> claims = new HashMap<>();
        claims.put("profileId", id);
        claims.put("role", role.name()); // Roleni String ko'rinishida qo'shamiz
        claims.put("type", "ACCESS_TOKEN"); // Token turini aniqlash uchun

        return Jwts.builder()
                .setClaims(claims) // Qo'shimcha ma'lumotlar
                .setSubject(username) // Asosiy foydalanuvchi (username)
                .setIssuedAt(now) // Yaratilgan vaqt
                .setExpiration(expiryDate) // Amal qilish muddati
                // Yangi tuzatilgan Key obyektidan foydalanish
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

}