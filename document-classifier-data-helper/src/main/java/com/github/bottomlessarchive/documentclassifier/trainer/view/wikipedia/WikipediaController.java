package com.github.bottomlessarchive.documentclassifier.trainer.view.wikipedia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bottomlessarchive.documentclassifier.trainer.service.wikipedia.ContentCleaner;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
public class WikipediaController {

    private final ObjectMapper objectMapper;
    private final ContentCleaner contentCleaner;

    @GetMapping("/wikipedia/{articleName}")
    public String getTrainingDataOnArticle(@PathVariable String articleName) throws InterruptedException, IOException {
        JsonNode jsonNode = objectMapper.readTree(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=" + articleName + "&rvslots=%2A&rvprop=content&formatversion=2&format=json"));

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

        return "<pre>" + contentCleaner.cleanArticleContent(Files.readString(Path.of("content-converted"))) + "</pre>";
    }
}
