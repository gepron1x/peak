package org.gepron1x.peak.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AscentDetails(Long id,
                            String groupName,
                            Long mountainId,
                            String mountainName,
                            LocalDateTime start,
                            LocalDateTime end,
                            List<ClimberBrief> participants) {
}
