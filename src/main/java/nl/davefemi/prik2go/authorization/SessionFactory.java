package nl.davefemi.prik2go.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.data.dto.identity.UserAccountDTO;
import nl.davefemi.prik2go.data.dto.identity.UserSessionDTO;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionFactory {
    private final JWTKey jwtKey;

    public UserSessionDTO generateSession(UserAccountDTO user){
        String token = Jwts.builder().setSubject(user.getUser().toString())
                .setId(UUID.randomUUID().toString())
                .claim("ROLE", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+300000))
                .signWith(jwtKey.getKey(), SignatureAlgorithm.HS256)
                .compact();
        return createSession(user, token);
    }

    public Claims parseToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(jwtKey.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private UserSessionDTO createSession(UserAccountDTO user, String token){
        Claims claims = parseToken(token);
        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(user);
        session.setToken(token);
        session.setIssuedAt(claims.getIssuedAt().toInstant());
        session.setExpiresAt(claims.getExpiration().toInstant());
        session.setTokenId(UUID.fromString(claims.getId()));
        return session;
    }

}
