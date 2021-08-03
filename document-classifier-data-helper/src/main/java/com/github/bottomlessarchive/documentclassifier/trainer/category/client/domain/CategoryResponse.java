package com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Optional;

@Getter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponse {

    @JsonProperty("continue")
    private final CategoryResponseContinue continueHeader;
    private final CategoryResponseQuery query;

    public Optional<CategoryResponseContinue> getContinue() {
        return Optional.ofNullable(continueHeader);
    }
}
