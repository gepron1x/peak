package org.gepron1x.peak.config;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Ascent;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.repository.AscentRepository;
import org.gepron1x.peak.repository.ClimberRepository;
import org.gepron1x.peak.repository.MountainRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Контрольный набор данных из ТЗ для демонстрации. Включается свойством
 * {@code app.seed-demo-data=true} и наполняет только пустую базу.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {

    private final MountainRepository mountains;
    private final ClimberRepository climbers;
    private final AscentRepository ascents;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (mountains.count() > 0 || climbers.count() > 0 || ascents.count() > 0) {
            return;
        }
        Mountain m1 = mountain("Гора М1", 5642, "Россия", "Кавказ");
        Mountain m2 = mountain("Гора М2", 4506, "Россия", "Алтай");
        Climber a = climber("Иванов", "Иван", "Иванович",
                "+7 (900) 111-22-33", "г. Москва, ул. Вымышленная, д. 1");
        Climber b = climber("Петрова", "Мария", "Сергеевна",
                "+7 (900) 222-33-44", "г. Москва, ул. Тестовая, д. 2");

        ascent("Группа В1", m1,
                LocalDateTime.of(2026, 6, 1, 8, 0),
                LocalDateTime.of(2026, 6, 3, 18, 0), a, b);
        ascent("Группа В2", m1,
                LocalDateTime.of(2026, 6, 10, 8, 0),
                LocalDateTime.of(2026, 6, 12, 18, 0), a);
        ascent("Группа В3", m2,
                LocalDateTime.of(2026, 6, 15, 8, 0),
                null, b);
    }

    private Mountain mountain(String name, int height, String country, String region) {
        Mountain mountain = new Mountain();
        mountain.setName(name);
        mountain.setHeight(height);
        mountain.setCountry(country);
        mountain.setRegion(region);
        return mountains.save(mountain);
    }

    private Climber climber(String lastName, String firstName, String middleName,
                            String phone, String address) {
        Climber climber = new Climber();
        climber.setLastName(lastName);
        climber.setFirstName(firstName);
        climber.setMiddleName(middleName);
        climber.setPhone(phone);
        climber.setAddress(address);
        return climbers.save(climber);
    }

    private void ascent(String groupName, Mountain mountain, LocalDateTime start,
                        LocalDateTime end, Climber... participants) {
        Ascent ascent = new Ascent();
        ascent.setGroupName(groupName);
        ascent.setMountain(mountain);
        ascent.setStartTime(start);
        ascent.setEndTime(end);
        ascents.save(ascent);
        for (Climber participant : participants) {
            ascent.addParticipant(participant);
        }
    }
}
