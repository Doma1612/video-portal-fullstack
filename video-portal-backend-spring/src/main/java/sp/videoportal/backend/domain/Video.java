package sp.videoportal.backend.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "Video")
public class Video {

    @Id
    @SequenceGenerator(name = "VIDEO_ID", sequenceName = "VIDEO_SEQ_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VIDEO_ID")
    private Long videoId;

    private String titel;

    private String beschreibung;

    private String dateipfad;

    private String metaData;

    private int aufrufZaehler;

    @ManyToOne(optional = false)
    private Thema thema;

    @ElementCollection
    @CollectionTable(name = "Unterkategorie_in_video", joinColumns = @JoinColumn(name = "videoId"))
    @Column(name = "unterkategorieId")
    private Collection<Long> unterKategorien = new ArrayList<>();

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getDateipfad() {
        return dateipfad;
    }

    public void setDateipfad(String dateipfad) {
        this.dateipfad = dateipfad;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public int getAufrufZaehler() {
        return aufrufZaehler;
    }

    public void setAufrufZaehler(int aufrufZaehler) {
        this.aufrufZaehler = aufrufZaehler;
    }

    public void incrementAufrufZaehler() {
        this.aufrufZaehler++;
    }

    public Thema getThema() {
        return thema;
    }

    public void setThema(Thema thema) {
        this.thema = thema;
    }

    public Collection<Long> getUnterKategorien() {
        return unterKategorien;
    }

    public void setUnterKategorien(Collection<Long> unterKategorien) {
        this.unterKategorien = unterKategorien;
    }
}
