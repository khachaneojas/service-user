package com.sprk.service.user.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveEmployeeRequest {
    private List<String> days;
    private Instant in_time;
    private Instant out_time;
    private Integer leaves;
    private String req_id;
}
