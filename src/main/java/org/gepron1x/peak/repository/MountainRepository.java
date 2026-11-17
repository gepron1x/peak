package org.gepron1x.peak.repository;

import java.util.List;
import org.gepron1x.peak.domain.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MountainRepository extends JpaRepository<Mountain, Long> {

    List<Mountain> findAllByOrderByNameAscIdAsc();

    boolean existsByNameIgnoreCaseAndCountryIgnoreCaseAndRegionIgnoreCase(
            String name, String country, String region);

    boolean existsByNameIgnoreCaseAndCountryIgnoreCaseAndRegionIgnoreCaseAndIdNot(
            String name, String country, String region, Long id);
}
