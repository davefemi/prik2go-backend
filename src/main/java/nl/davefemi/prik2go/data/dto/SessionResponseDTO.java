package nl.davefemi.prik2go.data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SessionResponseDTO {
    private UUID user;
    private String token;
    private Instant expiresAt;
}
