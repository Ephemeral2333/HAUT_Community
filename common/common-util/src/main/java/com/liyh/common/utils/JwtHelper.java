package com.liyh.common.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author LiYH
 * @Description JWT工具类
 * @Date 2023/3/13 9:39
 **/

public class JwtHelper {

    private static long tokenExpiration = 60 * 60 * 1000;   // 有效期5分钟
    private static String tokenSignKey = "liyh";   // 我的签名私钥

    // 生成Token
    public static String createToken(String userId, String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static String getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return null;
            }

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            String userId = (String) claims.get("userId");
            return userId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return "";
            }

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getExpirationDate(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return "";
            }

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return df.format(claims.getExpiration());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}