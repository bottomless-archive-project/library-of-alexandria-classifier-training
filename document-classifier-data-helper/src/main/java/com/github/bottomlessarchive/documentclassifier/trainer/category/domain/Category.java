package com.github.bottomlessarchive.documentclassifier.trainer.category.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Category {

    private final int id;
    private final String name;
}
