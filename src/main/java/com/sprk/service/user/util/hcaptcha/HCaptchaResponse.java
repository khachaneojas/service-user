package com.sprk.service.user.util.hcaptcha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;





@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HCaptchaResponse {
    private Boolean success;
    private String challenge_ts;
    private String hostname;
}
