package sp.videoportal.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Unterkategorie")
public class Unterkategorie {

    @Id
    @SequenceGenerator(
            name = "UNTERKATEGORIE_ID",
            sequenceName = "UNTERKATEGORIE_SEQ_ID",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UNTERKATEGORIE_ID")
    private Long id;

    private String name;

    @ManyToOne(optional = false)
    private Thema thema;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Thema getThema() {
        return thema;
    }

    public void setThema(Thema thema) {
        this.thema = thema;
    }
}
