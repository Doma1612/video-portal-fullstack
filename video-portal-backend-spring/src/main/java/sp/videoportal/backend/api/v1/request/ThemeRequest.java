package sp.videoportal.backend.api.v1.request;

import jakarta.validation.constraints.NotBlank;

public record ThemeRequest(@NotBlank String name) {}
