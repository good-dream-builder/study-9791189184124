package com.songko.study9791189184124.member.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Map;

@Log4j2
@Component
public class JWTUtil {
    // The specified key byte array is 80 bits which is not secure enough for any JWT HMAC-SHA algorithm.
    // The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).
    // Consider using the Jwts.SIG.HS256.key() builder (or HS384.key() or HS512.key()) to create a key guaranteed to be secure enough for your preferred HMAC-SHA algorithm.
    // See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.
    private static String key = "acrofuture-hyundaiautoever-poc-project";

    /**
     * JWT 문자열을 생성
     *
     * @param valueMap
     * @param min
     * @return
     */
    public String createToken(Map<String, Object> valueMap, int min) {
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))    // 만료시간 : 분 단위로 지정
                .claims(valueMap)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 문자열을 검증
     *
     * @param token
     * @return
     */
    public Map<String, Object> validateToken(String token) {
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // io.jsonwebtoken.JwtException : MalformedJwtException, ExpiredJwtException, InvalidClaim Exception
            throw new RuntimeException(e.getMessage());
        }

        Claims claims = Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();

        log.info("claims: {}", claims);
        return claims;
    }
}
