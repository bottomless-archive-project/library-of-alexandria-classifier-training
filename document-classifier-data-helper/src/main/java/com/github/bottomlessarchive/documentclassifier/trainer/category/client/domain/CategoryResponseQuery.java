package com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponseQuery {

    @JsonProperty("categorymembers")
    private final List<CategoryResponseMember> categoryMembers;
}
