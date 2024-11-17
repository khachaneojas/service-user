package com.sprk.service.user.util.hcaptcha;

import com.sprk.commons.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Component
public class HCaptchaUtils {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.hcaptcha.endpoint}")
    private String hCaptchaEndpoint;





    public boolean validateHCaptchaOnWeb(String hCaptchaResponse) {
        String siteKey = "609acdf5-4d7b-4735-906b-d1d3d4dcffed";
        String secretKey = "ES_2d0a8c579b484c759bbf1d4bcd20f655";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(hCaptchaEndpoint)
                .queryParam("sitekey", siteKey)
                .queryParam("secret", secretKey)
                .queryParam("response", hCaptchaResponse);
        String verificationUrl = builder.toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<?> response = restTemplate.exchange(
                    verificationUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(httpHeaders),
                    HCaptchaResponse.class
            );

            if (response.getBody() != null && response.getBody() instanceof HCaptchaResponse) {
                HCaptchaResponse responseBody = (HCaptchaResponse) response.getBody();
                if (Boolean.TRUE.equals(responseBody.getSuccess()))
                    return true;
            }
        } catch (Exception ex) {
            throw new InvalidDataException(ex.getMessage());
        }

        return false;
    }

}
