package nl.davefemi.prik2go.authorization;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JWTKey {
    private final SecretKey key;

    public JWTKey(@Value("${external.jwt.key}") String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public SecretKey getKey() {
        return key;
    }
}