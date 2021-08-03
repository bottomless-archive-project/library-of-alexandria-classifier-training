package com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponseContinue {

    private final String cmcontinue;
}
