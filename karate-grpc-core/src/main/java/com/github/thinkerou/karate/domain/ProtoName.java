package com.github.thinkerou.karate.domain;

/**
 * ProtoName
 *
 * @author thinkerou
 */
public final class ProtoName {

    private final String packageName;
    private final String serviceName;
    private final String methodName;

    /**
     * @param packageName package name
     * @param serviceName service name
     * @param methodName method name
     */
    public ProtoName(String packageName, String serviceName, String methodName) {
        this.packageName = packageName;
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    /**
     * @return string
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return string
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return string
     */
    public String getMethodName() {
        return methodName;
    }

}
