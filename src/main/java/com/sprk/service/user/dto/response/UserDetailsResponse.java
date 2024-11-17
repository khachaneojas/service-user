package com.sprk.service.user.dto.response;

import java.util.HashSet;
import java.util.Set;


import com.sprk.commons.document.dto.MainTab;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;





@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {
	private String emp_id;
	private String name;
	private String email;
	private boolean enabled = false;
	private Set<String> authorities = new HashSet<>();
	private boolean profile = false;
	private Set<MainTab> entitlements;
}
