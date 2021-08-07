package com.github.bottomlessarchive.documentclassifier.trainer.service.wikipedia;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ContentCleaner {

    public String cleanArticleContent(final String content) {
        return StringUtils.substringBefore(content, "References")
                .replace("\\\\n", System.lineSeparator())
                .replaceAll("\\{\\{.*?}}", " ")
                .replaceAll("\\[.*?\\]", " ")
                .replaceAll("<ref\\sname=\\\\\"[\\w \\d]+\\\\\"\\s*(\\/|)>(\\w+\\s\\(\\d+\\),\\s(\\d+|\\d+.\\d+)\\.|)", "")
                .replaceAll("=+", "")
                .replaceAll("\\(\\s*(,|)\\s*\\)", " ")
                .replaceAll("\\n\\s*", System.lineSeparator())
                .replace("\\\\\"", "\"")
                .replaceAll(" +", " ")
                .replace("\\u00a0", " ")
                .trim();
    }
}
