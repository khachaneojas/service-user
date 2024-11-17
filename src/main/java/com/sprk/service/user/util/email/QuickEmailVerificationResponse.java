package com.sprk.service.user.util.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;





@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuickEmailVerificationResponse {
    private String result;
    private String reason;
    private Boolean disposable;
    private Boolean accept_all;
    private Boolean role;
    private Boolean free;
    private String email;
    private String user;
    private String domain;
    private String mx_record;
    private String mx_domain;
    private Boolean safe_to_send;
    private String did_you_mean;
    private Boolean success;
    private String message;

}
