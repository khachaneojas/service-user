package com.sprk.service.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TEmployeeActivityResponse {
	private String emp_id;
	private String emp_name;
	private Set<String> emp_designation;
	private String emp_action;
	private Instant emp_action_at;
}
