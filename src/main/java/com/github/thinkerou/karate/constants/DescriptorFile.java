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
    PROTO("/target/generated-resources/protobuf/descriptor-sets/karate-grpc.protobin"),
    ;

    private final String text;

    DescriptorFile(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
