package com.github.thinkerou.karate.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Helper
 *
 * @author thinkerou
 */
public class Helper {

    private static final Logger logger = Logger.getLogger(Helper.class.getName());

    public static void validatePath(Optional<Path> maybePath) {
        if (maybePath.isPresent()) {
            if (!Files.exists(maybePath.get())) {
                throw new IllegalArgumentException("Path not exist: " + maybePath.get().toString());
            }
        }
    }

    public static String readFile(String file) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            logger.warning(e.getMessage());
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String payloads = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            payloads += line + "\n";
        }

        bufferedReader.close();

        return payloads;
    }

}
