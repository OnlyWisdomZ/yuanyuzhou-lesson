package com.ming.util;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** @author Ming */
public class JwtUtil {

    /** JWT服务端密钥 */
    private static final String SECRET_KEY = "my-secret";
    /** JWT主题 */
    private static final String SUBJECT = "用户登录认证";
    /** JWT签发人 */
    private static final String ISSUER = "Ming";
    /** JWT过期时间（默认 2 小时） */
    private static final Long EXPIRES_TIME = 1000 * 60 * 60 * 2L;
    /** JWT即将过期的时间阈值（10 分钟） */
    private static final Long EXPIRATION_TIME_THRESHOLD = 1000 * 60 * 10L;

    /**
     * 生成用户对应的token字符串，默认将用户的主键，姓名和头像地址添加到token负载中
     *
     * @param id     用户的主键
     * @param name   用户的姓名，如真实姓名，昵称等
     * @param avatar 用户的头像
     * @return token字符串
     */
    public static String build(Long id,
                               String name,
                               String avatar) {
        // 创建自定义负载
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("name", name);
        claims.put("avatar", avatar);
        // 将时间戳加入负载，保证Token唯一性
        claims.put("created", System.currentTimeMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(SUBJECT)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRES_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    
    /**
     * 验证token字符串，验证失败直接抛出异常，
     * 验证成功后判断当前token是否即将过期，
     * 若是则生成新的token并返回，否则返回原token值
     *
     * @param token token字符串
     * @return token信息，若expireSoon为true，表示token即将过期，反之表示token不需要刷新
     */
    public static Map<String, Object> parse(String token) {
        Map<String, Object> result = new HashMap<>(4);
        if (StrUtil.isEmpty(token)) {
            throw new RuntimeException("token令牌为空");
        }
        try {
            // 解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            // 获取负载
            Long id = claims.get("id", Long.class);
            String name = claims.get("name", String.class);
            String avatar = claims.get("avatar", String.class);
            // 若token过期时间小于指定阈值，则重新生成token并返回
            if (claims.getExpiration().getTime() - System.currentTimeMillis() 
                    < EXPIRATION_TIME_THRESHOLD) {
                result.put("expiringSoon", true);
                result.put("newToken", build(id, name, avatar));
            }
            // 若token过期时间大于指定阈值，不需要重新生成token，将用户信息返回即可
            else {
                result.put("id", id);
                result.put("name", name);
                result.put("avatar", avatar);
                result.put("expiringSoon", false);
            }
        } catch (Exception e) {
            throw new RuntimeException("token解析失败");
        }
        return result;
    }

    /**
     * 验证token字符串，验证失败直接抛出异常，成功返回id值
     *
     * @param token token字符串
     * @return id值
     */
    public static Long getId(String token) {
        return (Long) parse(token).get("id");
    }

    /**
     * 验证token字符串，验证失败直接抛出异常，成功返回name值
     *
     * @param token token字符串
     * @return name值
     */
    public static String getName(String token) {
        return (String) parse(token).get("name");
    }

    /**
     * 验证token字符串，验证失败直接抛出异常，成功返回avatar值
     *
     * @param token token字符串
     * @return avatar值
     */
    public static String getAvatar(String token) {
        return (String) parse(token).get("avatar");
    }
}
