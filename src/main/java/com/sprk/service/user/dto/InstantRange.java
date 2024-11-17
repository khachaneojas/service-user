package com.sprk.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
public class InstantRange {
    private final Instant start;
    private final Instant end;

    public InstantRange(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }
}
