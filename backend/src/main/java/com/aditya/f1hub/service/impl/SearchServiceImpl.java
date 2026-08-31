package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.search.SearchResponseDto;
import com.aditya.f1hub.mapper.CircuitMapper;
import com.aditya.f1hub.mapper.ConstructorMapper;
import com.aditya.f1hub.mapper.DriverMapper;
import com.aditya.f1hub.mapper.RaceMapper;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.repository.CircuitRepository;
import com.aditya.f1hub.repository.ConstructorRepository;
import com.aditya.f1hub.repository.DriverRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import com.aditya.f1hub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final int SEARCH_RESULT_LIMIT = 10;

    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;
    private final CircuitRepository circuitRepository;
    private final RaceRepository raceRepository;
    private final SeasonRepository seasonRepository;

    private final DriverMapper driverMapper;
    private final ConstructorMapper constructorMapper;
    private final CircuitMapper circuitMapper;
    private final RaceMapper raceMapper;

    @Override
    public SearchResponseDto search(String query) {

        String normalizedQuery = query.trim();

        Pageable pageable = PageRequest.of(0, SEARCH_RESULT_LIMIT);

        List<Integer> seasons = findSeason(normalizedQuery);

        return SearchResponseDto.builder()
                .drivers(
                        driverRepository.search(normalizedQuery, pageable)
                                .map(driverMapper::toResponseDto)
                                .getContent()
                )
                .constructors(
                        constructorRepository.search(normalizedQuery, pageable)
                                .map(constructorMapper::toResponseDto)
                                .getContent()
                )
                .circuits(
                        circuitRepository.search(normalizedQuery, pageable)
                                .map(circuitMapper::toResponseDto)
                                .getContent()
                )
                .races(
                        raceRepository.search(normalizedQuery, pageable)
                                .map(raceMapper::toResponseDto)
                                .getContent()
                )
                .seasons(seasons)
                .build();
    }

    private List<Integer> findSeason(String query) {

        try {
            Integer year = Integer.valueOf(query);

            return seasonRepository.findByYear(year)
                    .map(Season::getYear)
                    .map(List::of)
                    .orElseGet(List::of);

        } catch (NumberFormatException exception) {
            return List.of();
        }
    }
}