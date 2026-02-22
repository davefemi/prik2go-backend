package nl.davefemi.prik2go.data.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_session", referencedColumnName = "id")
    private UserSessionEntity userSession;

    @ManyToOne(optional = false)
    @JoinColumn(name ="provider", referencedColumnName = "id")
    private OAuthClientEntity provider;

    @Column(name = "user_authorized")
    private Boolean authorized;


}
