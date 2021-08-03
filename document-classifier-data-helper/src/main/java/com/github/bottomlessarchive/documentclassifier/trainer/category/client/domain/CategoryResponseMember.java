package com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class CategoryResponseMember {

    @JsonProperty("pageid")
    private final int id;
    private final String title;
}
