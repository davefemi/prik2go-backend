package nl.davefemi.prik2go.authorization;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordManager {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hashPassword(String password){
        return encoder.encode(password);
    }

    public boolean match(String password, String hash){
        return encoder.matches(password, hash);
    }
}
