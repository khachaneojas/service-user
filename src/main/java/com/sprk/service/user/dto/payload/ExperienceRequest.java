package com.sprk.service.user.dto.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.sprk.service.user.util.deserializer.CDateValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;
import java.time.Instant;








@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperienceRequest {
    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,40}$",
            message = "Position should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String position;

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsCyrillic}\\p{IsGreek}\\p{N}0-9, .+/&()'-]{2,200}$",
            message = "Position should be restricted to characters (A-Z, a-z, 0-9) and within the range of 2 to 200 characters.")
    private String company;

    @JsonDeserialize(using = CDateValidator.class)
    private Instant start;

    @JsonDeserialize(using = CDateValidator.class)
    private Instant end;
}
