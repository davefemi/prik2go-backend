package nl.davefemi.prik2go.data.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "oauth_client")
public class OAuthClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
