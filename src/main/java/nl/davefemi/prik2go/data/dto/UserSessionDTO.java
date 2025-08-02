package nl.davefemi.prik2go.data.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserSessionDTO {
    private Long id;
    private UserAccountDTO userId;
    private String token;
    private Instant issuedAt;
    private Instant expiresAt;
    private UUID tokenId;
}
