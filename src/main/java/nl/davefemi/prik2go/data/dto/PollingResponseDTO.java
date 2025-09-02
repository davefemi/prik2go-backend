package nl.davefemi.prik2go.data.dto;

import lombok.Data;
import nl.davefemi.prik2go.authorization.SecretGenerator;

import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Data
public class PollingResponseDTO {
    private UUID deviceCode = UUID.randomUUID();
    private String secret = SecretGenerator.generateSecret(48);
    private Long pollingInterval = 2000L;
    private Instant expiresAt = Instant.now().plusSeconds(3000);
    private String url = "http://localhost:8080/login/google?state=" + deviceCode;
}
