package org.gepron1x.peak.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.gepron1x.peak.domain.Ascent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AscentRepository extends JpaRepository<Ascent, Long> {

    boolean existsByMountainId(Long mountainId);

    long countByEndTimeIsNull();

    @Query("""
            select distinct a from Ascent a
            join fetch a.mountain
            left join fetch a.participations
            order by a.startTime desc, a.id desc
            """)
    List<Ascent> findAllForList();

    @Query("""
            select distinct a from Ascent a
            left join fetch a.participations p
            left join fetch p.climber
            where a.id = :id
            """)
    Optional<Ascent> findByIdWithParticipants(@Param("id") Long id);

    @Query("""
            select distinct a from Ascent a
            left join fetch a.participations p
            left join fetch p.climber
            where a.mountain.id = :mountainId
            order by a.startTime asc, a.id asc
            """)
    List<Ascent> findByMountainWithParticipants(@Param("mountainId") Long mountainId);

    @Query("""
            select a from Ascent a
            join fetch a.mountain
            where a.startTime < :upper
              and coalesce(a.endTime, :now) >= :lower
            order by a.startTime asc, a.id asc
            """)
    List<Ascent> findOverlapping(@Param("lower") LocalDateTime lower,
                                 @Param("upper") LocalDateTime upper,
                                 @Param("now") LocalDateTime now);
}
