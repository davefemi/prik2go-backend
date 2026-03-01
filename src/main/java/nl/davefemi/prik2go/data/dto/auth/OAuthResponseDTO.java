package nl.davefemi.prik2go.data.dto.auth;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class OAuthResponseDTO {
    private UUID requestCode;
    private String provider;
    private String secret;
    private Long pollingInterval;
    private Instant expiresAt;
    private String url;
}
