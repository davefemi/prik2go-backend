package nl.davefemi.prik2go.data.entity.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vestiging")
public class BranchEntity {

    @Id
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "plaats", referencedColumnName = "naam")
    private LocationEntity location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postcode", referencedColumnName = "postcode")
    private PostcodeInfoEntity postcode;
}
