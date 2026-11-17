package org.gepron1x.peak.service.dto;

import java.util.List;
import org.gepron1x.peak.domain.Climber;

public record ClimberStats(Climber climber, List<ClimberMountainCount> mountains) {
}
