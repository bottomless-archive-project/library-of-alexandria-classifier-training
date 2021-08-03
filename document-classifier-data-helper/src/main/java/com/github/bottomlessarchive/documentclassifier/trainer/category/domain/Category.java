package com.github.bottomlessarchive.documentclassifier.trainer.category.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Category {

    private final int id;
    private final String name;
}
