package nl.davefemi.prik2go.data.entity.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;


@Entity
@Data
@Table(name = "bezoek")
public class VisitEntity {
    @Id
    private Long nr;

    @Column(name = "datum")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "vestiging", referencedColumnName = "plaats")
    private BranchEntity branch;

    @ManyToOne
    @JoinColumn(name = "klant", referencedColumnName = "nr")
    private CustomerEntity customer;
}
