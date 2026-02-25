package nl.davefemi.prik2go.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name = "oauth_request_error")
public class OAuthRequestErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "request", referencedColumnName = "requestId")
    private OAuthRequestEntity requestId;

    @Column(name = "error")
    private String error;
}
