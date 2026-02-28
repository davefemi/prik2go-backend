package nl.davefemi.prik2go.data.entity.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "klant")
public class CustomerEntity {

    @Id
    private Long nr;

    @ManyToOne
    @JoinColumn(name = "postcode", referencedColumnName = "postcode")
    private PostcodeInfoEntity postcode;
}
