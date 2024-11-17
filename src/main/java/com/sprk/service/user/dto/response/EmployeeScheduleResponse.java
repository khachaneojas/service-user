package com.sprk.service.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeScheduleResponse {

    private List<String> days;
    private String in_time;
    private String out_time;
    private Integer leaves;
    private String req_id;

}
