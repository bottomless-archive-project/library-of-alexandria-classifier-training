package com.github.bottomlessarchive.documentclassifier.trainer.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain.CategoryResponse;
import com.github.bottomlessarchive.documentclassifier.trainer.category.client.domain.CategoryResponseContinue;
import com.github.bottomlessarchive.documentclassifier.trainer.category.domain.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryFactory {

    private final ObjectMapper objectMapper;

    public List<Category> queryCategoriesUnderCategory(final Category category) throws IOException {
        return queryCategoriesUnderCategory(category.getId());
    }

    public List<Category> queryCategoriesUnderCategory(final int categoryId) throws IOException {
        log.info("Loading categories under category with id: {}.", categoryId);

        final List<Category> result = new LinkedList<>();

        String nextPage = null;
        do {
            CategoryResponse categoryResponse;
            if (nextPage == null) {
                categoryResponse = objectMapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmpageid=" + categoryId + "&cmtype=subcat&format=json"),
                        CategoryResponse.class);

                nextPage = categoryResponse.getContinue()
                        .map(CategoryResponseContinue::getCmcontinue)
                        .orElse(null);
            } else {
                categoryResponse = objectMapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmpageid=" + categoryId + "&cmtype=subcat&format=json&cmcontinue=" + nextPage),
                        CategoryResponse.class);

                nextPage = categoryResponse.getContinue()
                        .map(CategoryResponseContinue::getCmcontinue)
                        .orElse(null);
            }

            final List<Category> categories = categoryResponse.getQuery().getCategoryMembers().stream()
                    .map(member -> Category.builder()
                            .id(member.getId())
                            .name(member.getTitle().replace("Category:", ""))
                            .build()
                    )
                    .collect(Collectors.toList());

            result.addAll(categories);
        } while (nextPage != null);

        return result;
    }
}
