package com.github.thinkerou.karate.message;

import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Output
 *
 * A one-stop-shop for output of the binary.
 * Supports writing to logs, to streams, to files, etc.
 *
 * @author thinkerou
 */
public interface Output extends AutoCloseable {

    // Writes a single string of output.
    void write(String content);

    // Writes a line of content.
    void writeLine(String content);

    // Writes a blank line.
    void newLine();

    static Output forFile(Path filePath) {
        return new OutputImpl(OutputImpl.PrintStreamWriter.forFile(filePath));
    }

    static Output forStream(PrintStream printStream) {
        return new OutputImpl(OutputImpl.PrintStreamWriter.forStream(printStream));
    }

}
