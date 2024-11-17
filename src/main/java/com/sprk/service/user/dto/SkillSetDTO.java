package com.sprk.service.user.dto;

import com.sprk.commons.entity.primary.user.tag.SkillClearanceStatus;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillSetDTO {
    private Long id;
    private String course;
    private SkillClearanceStatus status;
}
