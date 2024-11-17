package com.sprk.service.user.dto.response;

import com.sprk.commons.entity.primary.user.tag.LeaveStatus;
import com.sprk.commons.entity.primary.user.tag.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponse {
	private String leave_id;
	private String emp_id;
	private String name;
	private Instant start;
	private Instant end;
	private LeaveType type;
	private LeaveStatus status;
	private String reason;
	private String handled_by;
}
