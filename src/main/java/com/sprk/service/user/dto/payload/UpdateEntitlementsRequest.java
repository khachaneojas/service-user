package com.sprk.service.user.dto.payload;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprk.commons.document.dto.MainTab;
import com.sprk.service.user.util.deserializer.EMainTabValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEntitlementsRequest {
    @JsonDeserialize(using = EMainTabValidator.class)
    private HashSet<MainTab> entitlements;
}
