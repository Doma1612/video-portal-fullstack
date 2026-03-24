package sp.videoportal.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UnterkategorieDto {

    private Long id;

    @NotBlank
    private String name;

    @Valid
    @NotNull
    private ThemaDto thema;

    public UnterkategorieDto() {}

    public UnterkategorieDto(Long id, String name, ThemaDto thema) {
        this.id = id;
        this.name = name;
        this.thema = thema;
    }

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

    public ThemaDto getThema() {
        return thema;
    }

    public void setThema(ThemaDto thema) {
        this.thema = thema;
    }
}
