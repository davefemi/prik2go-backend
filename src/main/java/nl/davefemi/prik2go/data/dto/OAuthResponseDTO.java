package nl.davefemi.prik2go.data.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class OAuthResponseDTO {
    private UUID requestCode;
    private String secret;
    private Long pollingInterval;
    private Instant expiresAt;
    private String url;

    public OAuthResponseDTO(UUID requestCode,
                            String secret,
                            Long pollingInterval,
                            Instant expiresAt, String userId){
        this.requestCode = requestCode;
        this.secret = secret;
        this.pollingInterval = pollingInterval;
        this.expiresAt = expiresAt;
        this.url = String.format("https://prik2go-backend.onrender.com/oauth2/login/google?state=%s&uid=%s", requestCode, userId);
    }
}
