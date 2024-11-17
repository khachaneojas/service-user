package com.sprk.service.user.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDataResponse {
    private String contentType;
    private byte[] data;
}
