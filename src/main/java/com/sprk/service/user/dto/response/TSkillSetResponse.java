package com.sprk.service.user.dto.response;

import com.sprk.commons.entity.primary.user.tag.SkillClearanceStatus;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TSkillSetResponse {
	private Long id;
	private String name;
	private String hex;
	private String img;
	private SkillClearanceStatus status;
	private String certificate;
	private Integer attempts;
	private Instant issued_at;
	private Float obtained;
	private Float total;
}
