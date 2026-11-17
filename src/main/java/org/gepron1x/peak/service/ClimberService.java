package org.gepron1x.peak.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.repository.ClimberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClimberService {

    private final ClimberRepository climbers;

    @Transactional(readOnly = true)
    public List<Climber> findAll() {
        return climbers.findAllByOrderByLastNameAscFirstNameAscMiddleNameAscIdAsc();
    }

    @Transactional(readOnly = true)
    public Climber get(Long id) {
        return climbers.findById(id)
                .orElseThrow(() -> new BusinessException("Альпинист не найден"));
    }

    @Transactional
    public Climber create(String lastName, String firstName, String middleName,
                          String phone, String address) {
        String climberLastName = trim(lastName);
        String climberFirstName = trim(firstName);
        String climberMiddleName = trim(middleName);
        String climberPhone = trim(phone);
        String climberAddress = trim(address);
        if (climberLastName.isEmpty()) {
            throw new BusinessException("Укажите фамилию альпиниста");
        }
        if (climberFirstName.isEmpty()) {
            throw new BusinessException("Укажите имя альпиниста");
        }
        if (climberMiddleName.isEmpty()) {
            throw new BusinessException("Укажите отчество альпиниста");
        }
        if (climberPhone.isEmpty()) {
            throw new BusinessException("Укажите контактный телефон");
        }
        if (climberAddress.isEmpty()) {
            throw new BusinessException("Укажите адрес альпиниста");
        }
        Climber climber = new Climber();
        climber.setLastName(climberLastName);
        climber.setFirstName(climberFirstName);
        climber.setMiddleName(climberMiddleName);
        climber.setPhone(climberPhone);
        climber.setAddress(climberAddress);
        return climbers.save(climber);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
