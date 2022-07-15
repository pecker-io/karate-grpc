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
 * FileHelper
 *
 * @author thinkerou
 */
public final class FileHelper {

    private static final Logger logger = Logger.getLogger(FileHelper.class.getName());

    /**
     * @param maybePath maybe path
     */
    public static void validatePath(Optional<Path> maybePath) {
        if (maybePath.isPresent()) {
            if (!Files.exists(maybePath.get())) {
                throw new IllegalArgumentException("Path not exist: " + maybePath.get().toString());
            }
        }
    }

    /**
     * @param file file
     * @return string
     * @throws IOException io exception
     */
    public static String readFile(String file) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            logger.warning(e.getMessage());
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder payloads = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            payloads.append(line).append("\n");
        }

        bufferedReader.close();

        return payloads.toString();
    }

}
