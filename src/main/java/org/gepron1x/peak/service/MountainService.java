package org.gepron1x.peak.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.repository.AscentRepository;
import org.gepron1x.peak.repository.MountainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MountainService {

    private final MountainRepository mountains;
    private final AscentRepository ascents;

    @Transactional(readOnly = true)
    public List<Mountain> findAll() {
        return mountains.findAllByOrderByNameAscIdAsc();
    }

    @Transactional(readOnly = true)
    public Mountain get(Long id) {
        return mountains.findById(id)
                .orElseThrow(() -> new BusinessException("Гора не найдена"));
    }

    @Transactional(readOnly = true)
    public boolean hasAscents(Long id) {
        return ascents.existsByMountainId(id);
    }

    @Transactional
    public Mountain create(String name, Integer height, String country, String region) {
        String mountainName = trim(name);
        String mountainCountry = trim(country);
        String mountainRegion = trim(region);
        requireFields(mountainName, height, mountainCountry, mountainRegion);
        if (mountains.existsByNameIgnoreCaseAndCountryIgnoreCaseAndRegionIgnoreCase(
                mountainName, mountainCountry, mountainRegion)) {
            throw new BusinessException("Гора с такими названием, страной и районом уже существует");
        }
        Mountain mountain = new Mountain();
        apply(mountain, mountainName, height, mountainCountry, mountainRegion);
        return mountains.save(mountain);
    }

    @Transactional
    public Mountain update(Long id, String name, Integer height, String country, String region) {
        Mountain mountain = get(id);
        if (ascents.existsByMountainId(id)) {
            throw new BusinessException(
                    "Изменение запрещено: на вершину уже зарегистрированы восхождения");
        }
        String mountainName = trim(name);
        String mountainCountry = trim(country);
        String mountainRegion = trim(region);
        requireFields(mountainName, height, mountainCountry, mountainRegion);
        if (mountains.existsByNameIgnoreCaseAndCountryIgnoreCaseAndRegionIgnoreCaseAndIdNot(
                mountainName, mountainCountry, mountainRegion, id)) {
            throw new BusinessException("Гора с такими названием, страной и районом уже существует");
        }
        apply(mountain, mountainName, height, mountainCountry, mountainRegion);
        return mountain;
    }

    private static void requireFields(String name, Integer height, String country, String region) {
        if (name.isEmpty()) {
            throw new BusinessException("Укажите название вершины");
        }
        if (height == null || height < 1) {
            throw new BusinessException("Высота должна быть положительным числом метров");
        }
        if (country.isEmpty()) {
            throw new BusinessException("Укажите страну");
        }
        if (region.isEmpty()) {
            throw new BusinessException("Укажите район");
        }
    }

    private static void apply(Mountain mountain, String name, Integer height,
                              String country, String region) {
        mountain.setName(name);
        mountain.setHeight(height);
        mountain.setCountry(country);
        mountain.setRegion(region);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
