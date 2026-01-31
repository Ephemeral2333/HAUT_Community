package com.liyh.common.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author LiYH
 * @Description JWT工具类
 * @Date 2023/3/13 9:39
 **/
public class JwtHelper {

    private static final Logger log = LoggerFactory.getLogger(JwtHelper.class);

    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;   // 有效期24小时
    private static final String TOKEN_SIGN_KEY = "liyh";   // 签名私钥

    /**
     * 生成Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public static String createToken(String userId, String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SIGN_KEY)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID，解析失败返回null
     */
    public static String getUserId(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("userId");
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名，解析失败返回空字符串
     */
    public static String getUsername(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return "";
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取Token过期时间
     *
     * @param token JWT Token
     * @return 过期时间字符串，解析失败返回空字符串
     */
    public static String getExpirationDate(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return df.format(claims.getExpiration());
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            return "";
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return true-有效，false-无效
     */
    public static boolean isTokenValid(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(TOKEN_SIGN_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
}
