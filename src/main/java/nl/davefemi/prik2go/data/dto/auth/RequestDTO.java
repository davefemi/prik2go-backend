package nl.davefemi.prik2go.data.dto.auth;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestDTO {
    private UUID requestCode;
    private String provider;
    private String secret;
}
