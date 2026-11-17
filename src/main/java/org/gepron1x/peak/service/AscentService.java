package org.gepron1x.peak.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Ascent;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.domain.Participation;
import org.gepron1x.peak.repository.AscentRepository;
import org.gepron1x.peak.repository.ClimberRepository;
import org.gepron1x.peak.repository.MountainRepository;
import org.gepron1x.peak.service.dto.AscentDetails;
import org.gepron1x.peak.service.dto.AscentSummary;
import org.gepron1x.peak.service.dto.ClimberBrief;
import org.gepron1x.peak.service.dto.MountainAscents;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AscentService {

    private final AscentRepository ascents;
    private final MountainRepository mountains;
    private final ClimberRepository climbers;

    @Transactional(readOnly = true)
    public List<AscentSummary> findAll() {
        return ascents.findAllForList().stream()
                .map(ascent -> new AscentSummary(
                        ascent.getId(),
                        ascent.getGroupName(),
                        ascent.getMountain().getName(),
                        ascent.getStartTime(),
                        ascent.getEndTime(),
                        ascent.getParticipations().size()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AscentDetails getDetails(Long id) {
        Ascent ascent = ascents.findByIdWithParticipants(id)
                .orElseThrow(() -> new BusinessException("Восхождение не найдено"));
        return toDetails(ascent);
    }

    @Transactional(readOnly = true)
    public MountainAscents getMountainAscents(Long mountainId) {
        Mountain mountain = mountains.findById(mountainId)
                .orElseThrow(() -> new BusinessException("Гора не найдена"));
        List<AscentDetails> list = ascents.findByMountainWithParticipants(mountainId).stream()
                .map(AscentService::toDetails)
                .toList();
        return new MountainAscents(mountain, list);
    }

    @Transactional
    public Ascent create(String groupName, Long mountainId, LocalDateTime startTime) {
        String name = trim(groupName);
        if (name.isEmpty()) {
            throw new BusinessException("Укажите название группы");
        }
        if (mountainId == null) {
            throw new BusinessException("Выберите гору");
        }
        if (startTime == null) {
            throw new BusinessException("Укажите дату и время начала");
        }
        if (startTime.isAfter(LocalDateTime.now())) {
            throw new BusinessException("Дата и время начала не могут быть в будущем");
        }
        Mountain mountain = mountains.findById(mountainId)
                .orElseThrow(() -> new BusinessException("Выбранная гора не найдена"));
        Ascent ascent = new Ascent();
        ascent.setGroupName(name);
        ascent.setMountain(mountain);
        ascent.setStartTime(startTime);
        return ascents.save(ascent);
    }

    @Transactional
    public void finish(Long id, LocalDateTime endTime) {
        Ascent ascent = ascents.findById(id)
                .orElseThrow(() -> new BusinessException("Восхождение не найдено"));
        if (endTime == null) {
            throw new BusinessException("Укажите дату и время окончания");
        }
        if (ascent.getEndTime() != null) {
            throw new BusinessException("Восхождение уже завершено");
        }
        if (endTime.isBefore(ascent.getStartTime())) {
            throw new BusinessException("Дата окончания не может быть раньше даты начала");
        }
        if (endTime.isAfter(LocalDateTime.now())) {
            throw new BusinessException("Дата окончания не может быть в будущем");
        }
        ascent.setEndTime(endTime);
    }

    @Transactional
    public void addParticipant(Long ascentId, Long climberId) {
        Ascent ascent = ascents.findByIdWithParticipants(ascentId)
                .orElseThrow(() -> new BusinessException("Восхождение не найдено"));
        if (climberId == null) {
            throw new BusinessException("Выберите альпиниста");
        }
        Climber climber = climbers.findById(climberId)
                .orElseThrow(() -> new BusinessException("Выбранный альпинист не найден"));
        boolean alreadyParticipates = ascent.getParticipations().stream()
                .anyMatch(participation -> participation.getClimber().getId().equals(climberId));
        if (alreadyParticipates) {
            throw new BusinessException("Этот альпинист уже включен в состав восхождения");
        }
        ascent.addParticipant(climber);
    }

    private static AscentDetails toDetails(Ascent ascent) {
        List<ClimberBrief> participants = ascent.getParticipations().stream()
                .map(Participation::getClimber)
                .map(climber -> new ClimberBrief(climber.getId(), climber.getFullName(),
                        climber.getPhone(), climber.getAddress()))
                .sorted(Comparator.comparing(ClimberBrief::fullName).thenComparing(ClimberBrief::id))
                .toList();
        return new AscentDetails(
                ascent.getId(),
                ascent.getGroupName(),
                ascent.getMountain().getId(),
                ascent.getMountain().getName(),
                ascent.getStartTime(),
                ascent.getEndTime(),
                participants);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
