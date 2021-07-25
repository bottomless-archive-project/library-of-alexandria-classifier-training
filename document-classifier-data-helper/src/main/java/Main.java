import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String... args) throws IOException, InterruptedException {
        String title = "Cicero";

        ObjectMapper objectMapper = new ObjectMapper();

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
}
