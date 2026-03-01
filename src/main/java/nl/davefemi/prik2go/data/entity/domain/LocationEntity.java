package nl.davefemi.prik2go.data.entity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "plaats")
public class LocationEntity {

    @Id
    @Column(name = "naam")
    private String name;

}
