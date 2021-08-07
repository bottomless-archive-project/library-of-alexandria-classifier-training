package com.github.bottomlessarchive.trainer;

import com.github.bottomlessarchive.documentclassifier.service.Deeplearning4jDocumentClassifierFactory;
import com.github.bottomlessarchive.documentclassifier.service.DocumentClassifier;
import com.github.bottomlessarchive.documentclassifier.service.DocumentClassifierFactory;
import com.github.bottomlessarchive.documentclassifier.service.domain.ClassificationResult;
import com.github.bottomlessarchive.documentclassifier.service.domain.TrainingDocument;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ClassificationTest {

    @Test
    void testRomanMusic() throws URISyntaxException, IOException {
        DocumentClassifierFactory documentClassifierFactory = new Deeplearning4jDocumentClassifierFactory();

        final URI uri = ClassLoader.getSystemResource("training-data/").toURI();
        final String[] array = uri.toString().split("!");
        final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), new HashMap<>());
        final Path path = fs.getPath(array[1]);

        DocumentClassifier documentClassifier = documentClassifierFactory.newClassifier(
                loadTrainingDocuments(path));

        final ClassificationResult classificationResult = documentClassifier.classify(
                new String(new ClassPathResource("music-of-ancient-rome.wikipedia.txt").getInputStream().readAllBytes()));

        assertThat(classificationResult.getMostLikelyLabel(), is("history"));
    }

    @SneakyThrows
    private Collection<TrainingDocument> loadTrainingDocuments(final Path trainingDataPath) {
        return Files.list(trainingDataPath)
                .filter(Files::isDirectory)
                .flatMap(directory -> {
                    try {
                        return Files.list(directory)
                                .filter(Files::isRegularFile)
                                .map(document -> TrainingDocument.builder()
                                        .label(document.getName(1).toString())
                                        .content(loadFileContentForTraining(document))
                                        .build()
                                );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private String loadFileContentForTraining(final Path path) {
        return Files.lines(path)
                .collect(Collectors.joining(" "))
                .trim();
    }
}
