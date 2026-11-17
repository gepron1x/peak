package org.gepron1x.peak.repository;

import java.util.List;
import org.gepron1x.peak.domain.Climber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClimberRepository extends JpaRepository<Climber, Long> {

    List<Climber> findAllByOrderByLastNameAscFirstNameAscMiddleNameAscIdAsc();
}
