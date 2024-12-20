package com.task.Security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    protected static final Logger logger = LogManager.getLogger();

    private final String secretKey = "292c4ebac02150bc344bd198c8f48e5b5218d23722f717a7fc0bba3e4d807a0337faf994f99e61fda02a20fa551fbe66b9fd8f2e30f8232735f23ff7530bf346c3e447bd4e4db1c50de64f420eaa97822a69ece4c2d5dcd4878d29e9bf10647f2479ffa7a19faab1ac7e4a309da3ee5f5f95f126da1a7f12f00a05b75ff8cabb19c0f868334c5f5c8cf01209864f60dd28138dd68f4bcc9c0549bc784de3061ba1a6fd1cee5d337afabf6a2be205b93352a72dd4e3bde2924618e6bc9be5c9b55ce50331994b949a0a586985d8e9e3882bb294154308d1854ebe3c49aaa31c0fe7023288682fb50c4d2eb01586faa3323daf9a85348bdd1b4ce67c8a5e81583a";

    public String generateToken(String emailId) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(emailId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 1))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        try {
            final String username = extractUsername(token);
          
            if (isTokenExpired(token)) {
              
                return false;
            }
            logger.info("token is valid!");
            return username.equals(userDetails.getUsername());
        } catch (JwtException e) {
            logger.error("JwtException occured");
            return false;
        }
    }
}

