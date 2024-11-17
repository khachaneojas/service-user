package com.sprk.service.user.dto.payload;

import jakarta.validation.constraints.NotBlank;

import com.sprk.service.user.constant.PayloadMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;






@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {


	@NotBlank(message = PayloadMessage.BLANK_USERNAME_OR_EMAIL_LOGIN)
	private String eid;

	@NotBlank(message = PayloadMessage.BLANK_PASSWORD_LOGIN)
	private String password;

//	@NotBlank(message = "Cannot proceed further without hCaptcha response.")
	private String hcaptcha_response;
}
