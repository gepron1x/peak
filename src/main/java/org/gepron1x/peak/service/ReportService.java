package org.gepron1x.peak.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.repository.AscentRepository;
import org.gepron1x.peak.repository.ClimberRepository;
import org.gepron1x.peak.repository.MountainRepository;
import org.gepron1x.peak.repository.ParticipationRepository;
import org.gepron1x.peak.repository.ParticipationRepository.MountainClimberCountRow;
import org.gepron1x.peak.service.dto.ClimberBrief;
import org.gepron1x.peak.service.dto.ClimberMountainCount;
import org.gepron1x.peak.service.dto.ClimberStats;
import org.gepron1x.peak.service.dto.MountainClimberCount;
import org.gepron1x.peak.service.dto.PeriodAscent;
import org.gepron1x.peak.service.dto.PeriodReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AscentRepository ascents;
    private final ClimberRepository climbers;
    private final MountainRepository mountains;
    private final ParticipationRepository participations;

    /**
     * Ф04 и Ф07: восхождения и альпинисты, пересекающие период [from, to].
     */
    @Transactional(readOnly = true)
    public PeriodReport periodReport(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BusinessException("Укажите обе даты периода");
        }
        if (from.isAfter(to)) {
            throw new BusinessException("Дата начала периода не может быть позже даты окончания");
        }
        LocalDateTime lower = from.atStartOfDay();
        LocalDateTime upper = to.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<PeriodAscent> periodAscents = ascents.findOverlapping(lower, upper, now).stream()
                .map(ascent -> new PeriodAscent(
                        ascent.getId(),
                        ascent.getGroupName(),
                        ascent.getMountain().getName(),
                        ascent.getStartTime(),
                        ascent.getEndTime()))
                .toList();
        List<ClimberBrief> periodClimbers = participations
                .findClimbersOverlapping(lower, upper, now).stream()
                .sorted(Comparator.comparing(Climber::getId))
                .map(ReportService::brief)
                .toList();
        return new PeriodReport(periodAscents, periodClimbers);
    }

    /**
     * Ф06: завершенные восхождения выбранного альпиниста по каждой горе.
     */
    @Transactional(readOnly = true)
    public ClimberStats climberStats(Long climberId) {
        Climber climber = climbers.findById(climberId)
                .orElseThrow(() -> new BusinessException("Альпинист не найден"));
        Map<Long, Long> counted = byMountain(
                participations.countCompletedAscentsByMountainForClimber(climberId));
        List<ClimberMountainCount> rows = mountains.findAllByOrderByNameAscIdAsc().stream()
                .map(mountain -> new ClimberMountainCount(
                        mountain.getId(),
                        mountain.getName(),
                        counted.getOrDefault(mountain.getId(), 0L)))
                .toList();
        return new ClimberStats(climber, rows);
    }

    /**
     * Ф09: количество разных альпинистов в завершенных восхождениях по каждой горе.
     */
    @Transactional(readOnly = true)
    public List<MountainClimberCount> mountainStats() {
        Map<Long, Long> counted = byMountain(participations.countClimbersByMountain());
        return mountains.findAllByOrderByNameAscIdAsc().stream()
                .map(mountain -> new MountainClimberCount(
                        mountain.getId(),
                        mountain.getName(),
                        counted.getOrDefault(mountain.getId(), 0L)))
                .toList();
    }

    private static Map<Long, Long> byMountain(List<MountainClimberCountRow> rows) {
        return rows.stream().collect(Collectors.toMap(
                MountainClimberCountRow::getMountainId,
                MountainClimberCountRow::getTotal,
                Long::sum));
    }

    private static ClimberBrief brief(Climber climber) {
        return new ClimberBrief(climber.getId(), climber.getFullName(),
                climber.getPhone(), climber.getAddress());
    }
}
