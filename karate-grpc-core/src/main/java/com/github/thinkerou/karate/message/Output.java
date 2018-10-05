package com.github.thinkerou.karate.message;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Output
 *
 * @author thinkerou
 */
public class Output implements AutoCloseable {

    private final PrintStream printStream;

    Output(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void write(String content) {
        printStream.print(content);
    }

    public void writeLine(String content) {
        printStream.println(content);
    }

    public void newLine() {
        printStream.println();
    }

    public static Output forFile(Path path) {
        try {
            return new Output(new PrintStream(path.toString()));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Can't create writer for file: " + path, e);
        }
    }

    @Override
    public void close() {
        printStream.close();
    }

}
