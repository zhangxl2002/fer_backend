package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    // 3 到这个网站：https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=
    // 生成一个256位（以上）的秘钥
    private static final String SECRET_KEY = "MTMDWQe2Qjx6FAhXow4uyFduMIR+TpxSe6rC0fMzSXrjAqpVfQr4Z6yYKUjY4Ook";
    // 5 从claims中抽取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 6 从claims中抽取过期时间
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 4 抽取某个具体的Claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // 8.根据用户的信息产生token
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(
            Map<String, Object> extraClaims, //??? 这里的extraClaims有哪些
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // ???
    }
    // 7 判断token是否有效:其中包含的用户名和userDetails中的是否一致 token是否已经过期
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }



    //1 从token字符串中抽取出所有的Claim(需要使用秘钥进行验证)
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token) //??? 如果没有验证通过，如何处理？
                .getBody(); //??? 这里应该是获得payload的部分
    }

    //2 获取秘钥
    private Key getSignInKey() {  //??? 为什么要对秘钥再做Keys.hmacShaKeyFor处理
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // 这里应该单纯是根据keyBytes数据生成一个java.security.Key类型
    }


}
