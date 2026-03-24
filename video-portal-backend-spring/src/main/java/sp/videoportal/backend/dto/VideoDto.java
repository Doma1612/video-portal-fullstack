package sp.videoportal.backend.dto;

import java.util.Collection;

public class VideoDto {

    private Long videoId;
    private String titel;
    private String beschreibung;
    private String metaData;
    private int anzahlAufrufe;

    /** Display name used by the current frontend (search results). */
    private String name;

    private ThemaDto thema;
    private Collection<Long> unterKategorien;

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

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public int getAnzahlAufrufe() {
        return anzahlAufrufe;
    }

    public void setAnzahlAufrufe(int anzahlAufrufe) {
        this.anzahlAufrufe = anzahlAufrufe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThemaDto getThema() {
        return thema;
    }

    public void setThema(ThemaDto thema) {
        this.thema = thema;
    }

    public Collection<Long> getUnterKategorien() {
        return unterKategorien;
    }

    public void setUnterKategorien(Collection<Long> unterKategorien) {
        this.unterKategorien = unterKategorien;
    }
}
