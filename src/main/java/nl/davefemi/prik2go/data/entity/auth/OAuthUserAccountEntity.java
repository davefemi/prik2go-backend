package nl.davefemi.prik2go.data.entity.auth;

import jakarta.persistence.*;
import lombok.Data;
import nl.davefemi.prik2go.data.entity.identity.UserAccountEntity;

@Entity
@Data
@Table(name = "oauth_user_account")
public class OAuthUserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client", referencedColumnName = "id")
    private OAuthClientEntity client;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "useraccount", referencedColumnName = "id", unique = true)
    private UserAccountEntity userAccount;
}
