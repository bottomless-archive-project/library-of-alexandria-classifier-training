package com.github.bottomlessarchive.documentclassifier.trainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bottomlessarchive.documentclassifier.trainer.category.CategoryFactory;
import com.github.bottomlessarchive.documentclassifier.trainer.category.domain.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TrainerApplication implements CommandLineRunner {

    private static int MAIN_CATEGORY_ID = 7345184;

    private final CategoryFactory categoryFactory;

    public static void main(String... args) {
        SpringApplication.run(TrainerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final List<Category> mainCategories = categoryFactory.queryCategoriesUnderCategory(MAIN_CATEGORY_ID);

        final List<Category> testCategories = loadSubcategories(mainCategories.get(29));

        ObjectMapper objectMapper = new ObjectMapper();
        String title = "Cicero";

        JsonNode jsonNode = objectMapper.readTree(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=" + title + "&rvslots=%2A&rvprop=content&formatversion=2&format=json"));

        String content = jsonNode
                .get("query")
                .get("pages").get(0)
                .get("revisions").get(0)
                .get("slots")
                .get("main")
                .get("content")
                .asText();

        content = RegExUtils.removePattern(content, "<timeline>.*?</timeline>");

        try (BufferedWriter out = Files.newBufferedWriter(Path.of("content-to-convert"))) {
            out.write(content);
        }

        Process process = Runtime.getRuntime().exec("pandoc -f mediawiki -t plain -o content-converted content-to-convert --wrap=none");

        process.waitFor();

        String actual = Files.readString(Path.of("content-converted"));

        String result = StringUtils.substringBefore(actual, "References")
                .replaceAll("\\\\n", System.lineSeparator())
                .replaceAll("\\{\\{.*?}}", " ")
                .replaceAll("\\[.*?\\]", " ")
                .replaceAll("<ref\\sname=\\\\\"[\\w \\d]+\\\\\"\\s*(\\/|)>(\\w+\\s\\(\\d+\\),\\s(\\d+|\\d+.\\d+)\\.|)", "")
                .replaceAll("=+", "")
                .replaceAll("\\(\\s*(,|)\\s*\\)", " ")
                .replaceAll("\\n\\s*", System.lineSeparator())
                .replaceAll("\\\\\"", "\"")
                .replaceAll(" +", " ")
                .replaceAll("\\u00a0", " ")
                .trim();

        try (BufferedWriter out = Files.newBufferedWriter(Path.of("result.txt"))) {
            out.write(result);
        }

        System.out.println("Saved result to " + Path.of("result.txt").toAbsolutePath());
    }

    private List<Category> loadSubcategories(final Category category) throws IOException {
        log.info("Loading all subcategories under {}.", category);

        final List<Category> categories = categoryFactory.queryCategoriesUnderCategory(category);

        final List<Category> result = new LinkedList<>(categories);

        for (Category crawledCategory : categories) {
            result.addAll(loadSubcategories(crawledCategory));
        }

        return result;
    }
}
