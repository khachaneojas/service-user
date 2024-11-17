package com.sprk.service.user.dto.response;

import com.sprk.commons.entity.primary.user.tag.ToDoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToDoResponse {

    private String id;

    private Instant created_at;

    private String description;

    private ToDoStatus status;

}
