package com.sprk.service.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sprk.service.user.enums.*;

import com.sprk.service.user.util.serializer.CDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;





@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TEmployeeResponse {
	@JsonSerialize(using = CDateSerializer.class)
	private Instant joined_at;

	private String emp_id;
	private String name;
	private String email;
	private String phone;
	private EmployeeStatus employee_status;
	private boolean enabled;
	private Set<String> authorities = new HashSet<>();
	private Set<String> skill_set = new HashSet<>();
	private Set<String> skill_achieved = new HashSet<>();
}
