package org.gepron1x.peak.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.gepron1x.peak.domain.Climber;
import org.gepron1x.peak.domain.Participation;
import org.gepron1x.peak.domain.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    @Query("""
            select distinct p.climber from Participation p
            where p.ascent.startTime < :upper
              and coalesce(p.ascent.endTime, :now) >= :lower
            """)
    List<Climber> findClimbersOverlapping(@Param("lower") LocalDateTime lower,
                                          @Param("upper") LocalDateTime upper,
                                          @Param("now") LocalDateTime now);

    @Query("""
            select p.ascent.mountain.id as mountainId, count(distinct p.climber.id) as total
            from Participation p
            where p.ascent.endTime is not null
            group by p.ascent.mountain.id
            """)
    List<MountainClimberCountRow> countClimbersByMountain();

    @Query("""
            select p.ascent.mountain.id as mountainId, count(distinct p.ascent.id) as total
            from Participation p
            where p.climber.id = :climberId
              and p.ascent.endTime is not null
            group by p.ascent.mountain.id
            """)
    List<MountainClimberCountRow> countCompletedAscentsByMountainForClimber(
            @Param("climberId") Long climberId);

    interface MountainClimberCountRow {

        Long getMountainId();

        long getTotal();
    }
}
