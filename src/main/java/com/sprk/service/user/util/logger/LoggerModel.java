package com.sprk.service.user.util.logger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoggerModel {
    String remoteIp, remoteHost, userAgent, httpMethod, endPoint, responseStatus, methodName, message, identifier;
}
