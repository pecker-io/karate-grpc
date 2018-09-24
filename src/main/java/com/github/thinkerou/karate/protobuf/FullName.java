package com.github.thinkerou.karate.protobuf;

import com.github.thinkerou.karate.domain.ProtoName;

import io.grpc.MethodDescriptor;

/**
 * FullName
 *
 * @author thinkerou
 */
public class FullName {

    /**
     * format: <package>.<service>/<method>
     */
    public static ProtoName parse(String fullName) {
        String fullServiceName = MethodDescriptor.extractFullServiceName(fullName);
        if (fullServiceName == null) {
            throw new IllegalArgumentException("Can't extract full service from " + fullName);
        }

        int serviceLength = fullServiceName.length();
        if (serviceLength + 1 >= fullName.length() || fullName.charAt(serviceLength) != '/') {
            throw new IllegalArgumentException("Can't extract method name from " + fullName);
        }
        String methodName = fullName.substring(fullServiceName.length() + 1);

        int index = fullServiceName.lastIndexOf('.');
        if (index == -1) {
            throw new IllegalArgumentException("Can't extract package name from " + fullServiceName);
        }
        String packageName = fullServiceName.substring(0, index);

        if (index + 1 >= fullServiceName.length() || fullServiceName.charAt(index) != '.') {
            throw new IllegalArgumentException("Can't extract service from " + fullServiceName);
        }
        String serviceName = fullServiceName.substring(index + 1);

        return new ProtoName(packageName, serviceName, methodName);
    }

}
