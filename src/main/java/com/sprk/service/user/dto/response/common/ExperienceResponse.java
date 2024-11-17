package com.sprk.service.user.dto.response.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sprk.service.user.util.serializer.CDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExperienceResponse {

    private String position;
    private String company;

    @JsonSerialize(using = CDateSerializer.class)
    private Instant start;

    @JsonSerialize(using = CDateSerializer.class)
    private Instant end;

}
