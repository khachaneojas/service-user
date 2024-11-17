package com.sprk.service.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TNotificationPageDTO<T> {

    private Long totalCount;
    private Long unseenCount;
    private List<T> content;

}


