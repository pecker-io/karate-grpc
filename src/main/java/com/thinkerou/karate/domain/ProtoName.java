package com.thinkerou.karate.domain;

/**
 * ProtoName
 *
 * @author thinkerou
 */
public class ProtoName {

    private String packageName;
    private String serviceName;
    private String methodName;

    public ProtoName(String packageName, String serviceName, String methodName) {
        this.packageName = packageName;
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
