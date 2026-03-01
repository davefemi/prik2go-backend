package nl.davefemi.prik2go.data.entity.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "postcodeinfo")
public class PostcodeInfoEntity {
    @Id
    private String postcode;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lng")
    private float lng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plaats", referencedColumnName = "naam")
    private LocationEntity location;
}
