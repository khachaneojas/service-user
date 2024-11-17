package com.sprk.service.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TNotificationResponse {
	private Long notify_id;
	private Instant notify_at;
	private Boolean seen;
	private String message;
	private String view;
	private String ref_id;
}
