package com.sprk.service.user.dto.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddToDoRequest {
    private String description;
}
