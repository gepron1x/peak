package org.gepron1x.peak.service.dto;

import java.util.List;
import org.gepron1x.peak.domain.Mountain;

public record MountainAscents(Mountain mountain, List<AscentDetails> ascents) {
}
