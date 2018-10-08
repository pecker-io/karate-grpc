package com.github.thinkerou.grpc.web.controller;

import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * GrpcExecuteController
 *
 * @author thinkerou
 */
@RestController
@RequestMapping("/rest/n/grpc")
public class GrpcExcuteController {

    private static final Logger logger = Logger.getLogger(GrpcExcuteController.class.getName());

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public Object execute(@Valid @RequestBody Object request) {
        logger.info(request.toString());

        return null;
    }

}
