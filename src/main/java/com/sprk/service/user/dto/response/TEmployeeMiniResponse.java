package com.sprk.service.user.dto.response;

import com.sprk.commons.entity.primary.user.tag.SkillClearanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;





@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TEmployeeMiniResponse {
	private Long emp_id;
	private String uid;
	private String name;
	private SkillClearanceStatus status;
}
