package com.sprk.service.user.dto.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RectificationLinkRequest {

    @NotBlank(message = "Request-ID is mandatory in order to proceed.")
    private String req_id;

    @NotBlank(message = "Reason is mandatory in order to proceed.")
    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,200}$",
            message = "Reason should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String reason;
}
