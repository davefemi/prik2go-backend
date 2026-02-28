package nl.davefemi.prik2go.data.entity.identity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "user_account")
public class UserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "userid", columnDefinition = "uuid DEFAULT uuid_generate_v1()",
            insertable = false,
            updatable = false,
            nullable = false,
            unique = true
    )    private UUID userid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column (name = "role")
    private String role;

    @Column(name = "password")
    private String password;

}
