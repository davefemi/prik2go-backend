package nl.davefemi.prik2go.data.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SessionResponseDTO {
    private UUID user;
    private String token;
    private UUID tokenId;
    private Instant expiresAt;
}
