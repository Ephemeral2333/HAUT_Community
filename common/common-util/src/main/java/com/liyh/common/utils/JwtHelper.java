package com.liyh.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author LiYH
 * @Description JWT工具类（jjwt 0.12.x）
 * @Date 2023/3/13 9:39
 **/
public class JwtHelper {

    private static final Logger log = LoggerFactory.getLogger(JwtHelper.class);

    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;   // 有效期24小时
    private static final String TOKEN_SIGN_KEY = "liyh-haut-community-secret-key-32b";   // 签名私钥（至少32字节）

    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(TOKEN_SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成Token
     */
    public static String createToken(String userId, String username) {
        return Jwts.builder()
                .subject("AUTH-USER")
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SECRET_KEY)
                .compressWith(Jwts.ZIP.GZIP)
                .compact();
    }

    /**
     * 从Token中获取用户ID
     */
    public static String getUserId(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Claims claims = parseClaims(token);
            return claims == null ? null : (String) claims.get("userId");
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsername(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        try {
            Claims claims = parseClaims(token);
            return claims == null ? "" : (String) claims.get("username");
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取Token过期时间
     */
    public static String getExpirationDate(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        try {
            Claims claims = parseClaims(token);
            if (claims == null) return "";
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return df.format(claims.getExpiration());
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 验证Token是否有效
     */
    public static boolean isTokenValid(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            return parseClaims(token) != null;
        } catch (JwtException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    private static Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return null;
        }
    }
}
