package com.github.thinkerou.karate;

import com.github.thinkerou.karate.service.GrpcCall;
import com.github.thinkerou.karate.service.GrpcList;
import com.intuit.karate.core.ScenarioBridge;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GrpcClient
 *
 * @author thinkerou
 */
public class GrpcClient {

    protected static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

    protected GrpcCall callIns;
    protected GrpcList listIns;

    /**
     * @param host host
     * @param port port
     */
    public GrpcClient(String host, int port) {
        this.callIns = GrpcCall.create(host, port);
    }

    public GrpcClient() {
        this.listIns = GrpcList.create();
    }

    /**
     * @param name name
     * @param payload payload
     * @return string
     */
    public String call(String name, String payload) {
        return call(name, payload, null);
    }

    /**
     * @param name name
     * @param payload payload
     * @param scenarioBridge Karate ScenarioBridge
     * @return string
     */
    public String call(String name, String payload, ScenarioBridge scenarioBridge) {
        logRequest(payload, scenarioBridge);
        final String response = invokeCall(name, payload, scenarioBridge);
        logResponse(response, scenarioBridge);
        return response;
    }

    /**
     * @param name name
     * @param withMessage with message
     * @return string
     */
    public String list(String name, Boolean withMessage) {
        return list(name, withMessage, null);
    }

    /**
     * @param name name
     * @param withMessage with message
     * @param scenarioBridge scenario bridge
     * @return string
     */
    public String list(String name, Boolean withMessage, ScenarioBridge scenarioBridge) {
        logRequest(String.format("name=%s, withMessage=%s", name, withMessage), scenarioBridge);
        final String response = invokeList(name, withMessage);
        logResponse(response, scenarioBridge);
        return response;
    }

    /**
     * @param serviceFilter service filter
     * @param methodFilter method filter
     * @param withMessage with message
     * @return string
     */
    public String list(String serviceFilter, String methodFilter, Boolean withMessage) {
        return list(serviceFilter, methodFilter, withMessage, null);
    }

    /**
     * @param serviceFilter service filter
     * @param methodFilter method filter
     * @param withMessage with message
     * @param scenarioBridge scenario bridge
     * @return string
     */
    public String list(String serviceFilter, String methodFilter, Boolean withMessage,
            ScenarioBridge scenarioBridge) {
        logRequest(String.format("serviceFilter=%s, methodFilter=%s, withMessage=%s", serviceFilter,
                                 methodFilter, withMessage), scenarioBridge);
        final String response = invokeList(serviceFilter, methodFilter, withMessage);
        logResponse(response, scenarioBridge);
        return response;
    }

    protected static void logRequest(String message, ScenarioBridge scenarioBridge) {
        log("[request] " + message, scenarioBridge);
    }

    protected static void logResponse(String message, ScenarioBridge scenarioBridge) {
        log("[response] " + message, scenarioBridge);
    }

    protected static void log(String message, ScenarioBridge scenarioBridge) {
        if (scenarioBridge != null) {
            scenarioBridge.log(Value.asValue(message));
        }
        log.info(message);
    }

    protected String invokeCall(String name, String payload) {
        return callIns.invoke(name, payload, null);
    }

    protected String invokeCall(String name, String payload, ScenarioBridge scenarioBridge) {
        return callIns.invoke(name, payload, scenarioBridge);
    }

    protected String invokeList(String name, Boolean withMessage) {
        return listIns.invoke(name, withMessage);
    }

    protected String invokeList(String serviceFilter, String methodFilter, Boolean withMessage) {
        return listIns.invoke(serviceFilter, methodFilter, withMessage);
    }

}
