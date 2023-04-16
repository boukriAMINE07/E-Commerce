package com.boukriinfo.ecommerce.security.jwt;

import com.boukriinfo.ecommerce.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component
@Slf4j
public class JwtUtils {
    private static final Logger logger = Logger.getLogger(JwtUtils.class.getName());
   @Value("${boukriinfo.app.jwtSecret}")
    private String jwtSecret;


    @Value("${boukriinfo.app.jwtExpirationMs}")
    private int jwtExpirationMs;// 24h

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("userPrincipal.getUsername()"+userPrincipal.getUsername());


        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.warning("Invalid JWT signature: " + e.getMessage());
        }catch (MalformedJwtException e) {
            logger.warning("Invalid JWT token: " + e.getMessage());
        }catch (ExpiredJwtException e) {
            logger.warning("JWT token is expired: " + e.getMessage());
        }catch (UnsupportedJwtException e) {
            logger.warning("JWT token is unsupported: " + e.getMessage());
        }catch (IllegalArgumentException e) {
            logger.warning("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}
