package nl.davefemi.prik2go.data.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserAccountDTO {
    private Long id;
    private UUID user;
    private String name;
    private String email;
    private String role;
    private String password;
    private String newPassword;
}
