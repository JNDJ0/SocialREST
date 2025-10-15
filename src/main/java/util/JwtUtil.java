package util;

import io.jsonwebtoken.*;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
	private static final Key KEY = Keys.hmacShaKeyFor(
			"a_very_long_and_secure_secret_key_for_jwt_hs256_algorithm".getBytes(StandardCharsets.UTF_8)
    );

    private static final long ACCESS_TTL_SECONDS = 60 * 15; 
    private static final long REFRESH_TTL_SECONDS = 60L * 60 * 24 * 7;

    public static String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCESS_TTL_SECONDS)))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .claim("typ", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TTL_SECONDS)))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
    }

    public static long getAccessTtlSeconds() { return ACCESS_TTL_SECONDS; }
}
