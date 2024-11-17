package com.sprk.service.user.dto.payload;

import lombok.Data;

import java.util.Set;






@Data
public class TokenValidationRequest {
    private Set<String> views;
    private Set<String> actions;
}
