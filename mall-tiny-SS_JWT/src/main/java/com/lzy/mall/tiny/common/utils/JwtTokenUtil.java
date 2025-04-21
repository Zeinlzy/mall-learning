package com.lzy.mall.tiny.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    //sub 是 JWT 的“主体”，通常用于标识令牌的唯一所有者
    private static final String CLAIM_KEY_USERNAME = "sub";

    //iat 表示令牌的签发时间（Unix 时间戳），用于管理令牌的生命周期
    private static final String CLAIM_KEY_CREATED = "iat";

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    // 生成安全的 HMAC-SHA 密钥
    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
            * 生成 Token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return buildToken(claims);
        /*claims包含 JWT 的声明信息（如用户身份、权限等）
        *   claims.put("sub", "user123");       // 主题（用户唯一标识）
            claims.put("iat", new Date());      // 签发时间
            claims.put("roles", Arrays.asList("ADMIN", "USER")); // 自定义声明
        * */
    }

    /**
            * 构建 Token
            */
    private String buildToken(Map<String, Object> claims) {
        //创建一个 JwtBuilder 实例，用于逐步构建 JWT,JWT 由 Header（头）,Payload（载荷） 和 Signature（签名）三部分组成
        return Jwts.builder()
                .setClaims(claims) //设置 JWT 的 Payload（声明部分）
                .setExpiration(generateExpirationDate()) //设置 JWT 的过期时间
                .signWith(generateKey(), SignatureAlgorithm.HS512) //使用指定算法和密钥对 JWT 进行签名，防止数据篡改
                .compact(); //生成最终的 JWT 字符串
    }

    /**
     * 解析并验证 JWT 令牌，提取 Claims (负载)
     * 该方法会验证令牌的签名和有效期。
     *
     * @param token 要解析的 JWT 令牌字符串
     * @return 如果令牌有效并解析成功，返回 Claims 对象；否则返回 null
     */
    public Claims parseToken(String token) {
        try {
            // 使用配置的密钥构建 JWT 解析器
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey())  // 设置用于签名验证的密钥
                    .build()                       // 构建解析器实例
                    .parseClaimsJws(token)         // 解析 JWS (带有签名的JWT)，并验证签名
                    .getBody();                    // 如果验证成功，获取 JWT 的 Claims (负载)
        } catch (ExpiredJwtException e) {
            // 捕获 Token 过期异常
            LOGGER.error("Token 已过期: {}", e.getMessage());
        } catch (SecurityException | MalformedJwtException e) {
            // 捕获 Token 非法（如签名错误、格式错误）异常
            LOGGER.error("Token 非法: {}", e.getMessage());
        } catch (JwtException e) {
            // 捕获其他 JWT 解析相关的通用异常
            LOGGER.error("Token 解析失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // 捕获非法参数异常，例如token字符串为空或null
            LOGGER.error("Token 字符串无效: {}", e.getMessage());
        }
        // 如果解析或验证过程中发生任何异常，返回 null
        return null;
    }

    /**
            * 验证 Token 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
            * 提取用户名
     */
    public String extractUsername(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
            * 判断 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate != null && expirationDate.before(new Date());
    }

    /**
            * 获取过期时间
     */
    private Date getExpirationDate(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
            * 生成过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
            * 刷新 Token（仅当未过期时可刷新）
            */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null || isTokenExpired(token)) {
            return null;
        }
        claims.setIssuedAt(new Date()); // 更新签发时间
        return buildToken(claims);
    }
}