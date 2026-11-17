package org.gepron1x.peak.service.dto;

import java.time.LocalDateTime;

public record PeriodAscent(Long id,
                           String groupName,
                           String mountainName,
                           LocalDateTime start,
                           LocalDateTime end) {
}
