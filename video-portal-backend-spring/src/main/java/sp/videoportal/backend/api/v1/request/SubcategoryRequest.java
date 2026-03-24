package sp.videoportal.backend.api.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubcategoryRequest(@NotBlank String name, @NotNull Long themeId) {}
