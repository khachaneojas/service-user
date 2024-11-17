package com.sprk.service.user.util.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.logging.LogLevel;



public class LoggerManager {

    private final static String messagePattern = "[IP: {}] --- [Host: {}] --- [UserAgent: {}] --- [HttpMethod: {}] --- [HttpResponse: {}] --- [Endpoint: {}] --- [ID: {}]--- [Method: {}] --- [Message: {}]";

    public static void log(Class<?> exceptionClass, LoggerModel loggerModel, LogLevel logLevel) {
        mainLogger(exceptionClass, loggerModel, logLevel);
    }

    private static void mainLogger(Class<?> exceptionClass, LoggerModel loggerModel, LogLevel logLevel) {
        Logger logger = LoggerFactory.getLogger(exceptionClass);
        switch (logLevel) {
            case ERROR:
                logger.error(messagePattern, getLogParameters(loggerModel));
                break;
            case WARN:
                logger.warn(messagePattern, getLogParameters(loggerModel));
                break;
            case DEBUG:
                logger.debug(messagePattern, getLogParameters(loggerModel));
                break;
            case INFO:
                logger.info(messagePattern, getLogParameters(loggerModel));
                break;
        }
    }

    private static Object[] getLogParameters(LoggerModel loggerModel) {
        return new Object[] {
                loggerModel.getRemoteIp(),
                loggerModel.getRemoteHost(),
                loggerModel.getUserAgent(),
                loggerModel.getHttpMethod(),
                loggerModel.getResponseStatus(),
                loggerModel.getEndPoint(),
                loggerModel.getIdentifier(),
                loggerModel.getMethodName(),
                loggerModel.getMessage()
        };
    }
}
