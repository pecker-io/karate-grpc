package com.github.thinkerou.karate.constants;

/**
 * DescriptorFile
 *
 * @author thinkerou
 */
public enum DescriptorFile {

    /**
     * The value is correlated with descriptorSetFileName/descriptorSetOutputDirectory in pom file.
     * Please see https://www.xolstice.org/protobuf-maven-plugin/compile-mojo.html about more details.
     */
    PROTO_PATH("/.karate-grpc/protobuf-descriptor-sets/"),
    PROTO_FILE("karate-grpc.protobin"),
    ;

    private final String text;

    /**
     * @param text text
     */
    DescriptorFile(final String text) {
        this.text = text;
    }

    /**
     * @return string
     */
    public String getText() {
        return text;
    }

}
