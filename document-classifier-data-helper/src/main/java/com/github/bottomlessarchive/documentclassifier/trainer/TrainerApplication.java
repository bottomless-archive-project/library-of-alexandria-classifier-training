package com.github.bottomlessarchive.documentclassifier.trainer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class TrainerApplication {

    public static void main(String... args) {
        SpringApplication.run(TrainerApplication.class, args);
    }
}
