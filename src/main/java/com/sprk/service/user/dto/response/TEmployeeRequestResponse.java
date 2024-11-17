package com.sprk.service.user.dto.response;

import com.sprk.commons.entity.primary.user.tag.EmployeeRequestStatus;
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
public class TEmployeeRequestResponse {
	private Instant requested_at;
	private String req_id;
	private String name;
	private String email;
	private String phone;
	private EmployeeRequestStatus request_status;
	private Set<String> authorities = new HashSet<>();
}
