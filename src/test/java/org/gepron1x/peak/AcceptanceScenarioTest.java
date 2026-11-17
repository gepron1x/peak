package org.gepron1x.peak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gepron1x.peak.domain.Ascent;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.service.AscentService;
import org.gepron1x.peak.service.BusinessException;
import org.gepron1x.peak.service.ClimberService;
import org.gepron1x.peak.service.MountainService;
import org.gepron1x.peak.service.ReportService;
import org.gepron1x.peak.service.dto.AscentDetails;
import org.gepron1x.peak.service.dto.AscentSummary;
import org.gepron1x.peak.service.dto.ClimberMountainCount;
import org.gepron1x.peak.service.dto.MountainClimberCount;
import org.gepron1x.peak.service.dto.PeriodReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Проверки бизнес-правил и отчетов на наборе данных, аналогичном контрольному набору ТЗ.
 */
@SpringBootTest
@Transactional
class AcceptanceScenarioTest {

    @Autowired
    private MountainService mountainService;

    @Autowired
    private ClimberService climberService;

    @Autowired
    private AscentService ascentService;

    @Autowired
    private ReportService reportService;

    private LocalDate base;
    private Mountain m1;
    private Mountain m2;
    private Climber a;
    private Climber b;
    private Ascent v1;
    private Ascent v2;
    private Ascent v3;

    @BeforeEach
    void setUp() {
        base = LocalDate.now().minusDays(20);
        m1 = mountainService.create("Гора М1", 5642, "Россия", "Кавказ");
        m2 = mountainService.create("Гора М2", 4506, "Россия", "Алтай");
        a = climberService.create("Иванов", "Иван", "Иванович",
                "+7 (900) 111-22-33", "г. Москва, ул. Вымышленная, д. 1");
        b = climberService.create("Петрова", "Мария", "Сергеевна",
                "+7 (900) 222-33-44", "г. Москва, ул. Тестовая, д. 2");

        v1 = ascentService.create("Группа В1", m1.getId(), at(0, 8));
        ascentService.addParticipant(v1.getId(), a.getId());
        ascentService.addParticipant(v1.getId(), b.getId());
        ascentService.finish(v1.getId(), at(2, 18));

        v2 = ascentService.create("Группа В2", m1.getId(), at(9, 8));
        ascentService.addParticipant(v2.getId(), a.getId());
        ascentService.finish(v2.getId(), at(11, 18));

        v3 = ascentService.create("Группа В3", m2.getId(), at(14, 8));
        ascentService.addParticipant(v3.getId(), b.getId());
    }

    @Test
    void mountainWithAscentsCannotBeChanged() {
        assertThatThrownBy(() -> mountainService.update(
                m1.getId(), "Новое имя", 6000, "Россия", "Кавказ"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("запрещено");

        Mountain m3 = mountainService.create("Гора М3", 3000, "Россия", "Урал");
        mountainService.update(m3.getId(), "Гора М3 новая", 3100, "Россия", "Урал");

        assertThat(mountainService.get(m3.getId()).getName()).isEqualTo("Гора М3 новая");
        assertThat(mountainService.get(m3.getId()).getHeight()).isEqualTo(3100);
        assertThat(mountainService.get(m1.getId()).getName()).isEqualTo("Гора М1");
    }

    @Test
    void duplicateMountainIsRejectedIgnoringCase() {
        assertThatThrownBy(() -> mountainService.create("гора м1", 5642, "россия", "кавказ"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void duplicateParticipantIsRejected() {
        assertThatThrownBy(() -> ascentService.addParticipant(v1.getId(), b.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("уже включен");

        AscentDetails details = ascentService.getDetails(v1.getId());
        assertThat(details.participants()).hasSize(2);
    }

    @Test
    void invalidCompletionIsRejected() {
        assertThatThrownBy(() -> ascentService.finish(v3.getId(), at(13, 8)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("раньше");
        assertThatThrownBy(() -> ascentService.finish(v3.getId(), LocalDateTime.now().plusDays(1)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("будущем");

        ascentService.finish(v3.getId(), at(17, 18));
        assertThat(ascentService.getDetails(v3.getId()).end()).isEqualTo(at(17, 18));
    }

    @Test
    void periodReportContainsAscentsAndDistinctClimbers() {
        PeriodReport report = reportService.periodReport(base.plusDays(1), base.plusDays(1));

        assertThat(report.ascents()).extracting(row -> row.groupName()).containsExactly("Группа В1");
        assertThat(report.climbers()).extracting(row -> row.fullName())
                .containsExactly("Иванов Иван Иванович", "Петрова Мария Сергеевна");

        PeriodReport second = reportService.periodReport(base.plusDays(10), base.plusDays(10));
        assertThat(second.ascents()).extracting(row -> row.groupName()).containsExactly("Группа В2");
        assertThat(second.climbers()).extracting(row -> row.fullName())
                .containsExactly("Иванов Иван Иванович");

        PeriodReport unfinished = reportService.periodReport(base.plusDays(15), base.plusDays(15));
        assertThat(unfinished.ascents()).extracting(row -> row.groupName()).containsExactly("Группа В3");
        assertThat(unfinished.climbers()).extracting(row -> row.fullName())
                .containsExactly("Петрова Мария Сергеевна");
    }

    @Test
    void periodReportIsOrderedByStart() {
        PeriodReport report = reportService.periodReport(base.plusDays(2), base.plusDays(9));
        assertThat(report.ascents()).extracting(row -> row.groupName())
                .containsExactly("Группа В1", "Группа В2");
    }

    @Test
    void invalidPeriodIsRejected() {
        assertThatThrownBy(() -> reportService.periodReport(base.plusDays(5), base))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("позже");
    }

    @Test
    void climberStatisticsIncludeAllMountainsAndZeros() {
        List<ClimberMountainCount> stats = reportService.climberStats(a.getId()).mountains();
        Map<String, Long> byMountain = stats.stream().collect(Collectors.toMap(
                ClimberMountainCount::mountainName, ClimberMountainCount::ascents));

        assertThat(byMountain).containsEntry("Гора М1", 2L).containsEntry("Гора М2", 0L);

        Map<String, Long> forB = reportService.climberStats(b.getId()).mountains().stream()
                .collect(Collectors.toMap(ClimberMountainCount::mountainName,
                        ClimberMountainCount::ascents));
        assertThat(forB).containsEntry("Гора М1", 1L).containsEntry("Гора М2", 0L);
    }

    @Test
    void mountainStatisticsCountDistinctClimbersOfCompletedAscents() {
        Map<String, Long> before = reportService.mountainStats().stream()
                .collect(Collectors.toMap(MountainClimberCount::mountainName,
                        MountainClimberCount::climbers));
        assertThat(before).containsEntry("Гора М1", 2L).containsEntry("Гора М2", 0L);

        ascentService.finish(v3.getId(), at(17, 18));

        Map<String, Long> after = reportService.mountainStats().stream()
                .collect(Collectors.toMap(MountainClimberCount::mountainName,
                        MountainClimberCount::climbers));
        assertThat(after).containsEntry("Гора М1", 2L).containsEntry("Гора М2", 1L);
    }

    @Test
    void ascentListContainsGroupAndParticipantCount() {
        List<AscentSummary> list = ascentService.findAll();
        assertThat(list).isNotEmpty();
        AscentSummary first = list.getFirst();
        assertThat(first.groupName()).isEqualTo("Группа В3");
        assertThat(first.participantCount()).isEqualTo(1);
    }

    private LocalDateTime at(int dayOffset, int hour) {
        return base.plusDays(dayOffset).atTime(hour, 0);
    }
}
