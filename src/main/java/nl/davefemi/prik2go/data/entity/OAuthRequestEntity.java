package nl.davefemi.prik2go.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "oauth_request")
public class OAuthRequestEntity {
    @Id
    private UUID requestId;

    @Column(name = "secret")
    private String secret;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_session", referencedColumnName = "id")
    private UserSessionEntity userSession;

    @Column(name = "user_authorized")
    private Boolean authorized;
}
