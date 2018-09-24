package com.github.thinkerou.karate.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * Helper
 *
 * @author thinkerou
 */
public class Helper {

    public static void validatePath(Optional<Path> maybePath) {
        if (maybePath.isPresent()) {
            Preconditions.checkArgument(Files.exists(maybePath.get()));
        }
    }

    public static String readFile(String file) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
