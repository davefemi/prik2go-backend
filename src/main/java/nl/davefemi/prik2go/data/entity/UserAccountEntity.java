package nl.davefemi.prik2go.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Data
@Table(name = "user_account")
public class UserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "user", columnDefinition = "uuid DEFAULT uuid_generate_v1()",
            insertable = false,
            updatable = false,
            nullable = false
    )    private UUID user;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column (name = "role")
    private String role;

    @Column(name = "password")
    private String password;

}
