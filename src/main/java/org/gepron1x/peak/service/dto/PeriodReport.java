package org.gepron1x.peak.service.dto;

import java.util.List;

public record PeriodReport(List<PeriodAscent> ascents, List<ClimberBrief> climbers) {
}
