package nl.davefemi.prik2go.data.entity.auth;

import jakarta.persistence.*;
import lombok.Data;
import nl.davefemi.prik2go.data.entity.identity.UserSessionEntity;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "oauth_request")
public class OAuthRequestEntity {
    @Id
    @Column(name = "request_id")
    private UUID requestId;

    @Column(name = "secret")
    private String secret;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_session", referencedColumnName = "id")
    private UserSessionEntity userSession;

    @ManyToOne(optional = false)
    @JoinColumn(name ="provider", referencedColumnName = "id")
    private OAuthClientEntity provider;

    @Column(name = "user_authorized")
    private Boolean authorized;


}
