package com.sprk.service.user.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyLeaveRequest {
    private Instant start;
    private Instant end;
    private String type;
    private String reason;
}
