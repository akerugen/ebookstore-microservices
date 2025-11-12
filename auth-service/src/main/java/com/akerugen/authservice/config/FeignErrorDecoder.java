package com.akerugen.authservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Обработчик ошибок Feign
 * Преобразует HTTP ошибки от user-service в понятные исключения
 */
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LogManager.getLogger(FeignErrorDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        logger.error("Feign error on method: {}, status: {}", methodKey, response.status());

        switch (response.status()) {
            case 400:
                logger.error("Bad Request from user-service");
                return new RuntimeException("Invalid request to user-service");
            case 404:
                logger.error("User not found in user-service");
                return new RuntimeException("User not found");
            case 500:
                logger.error("Internal error in user-service");
                return new RuntimeException("user-service internal error");
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
