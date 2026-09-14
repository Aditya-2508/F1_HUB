package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.integration.dto.SeasonSyncResponseDto;
import com.aditya.f1hub.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeasonSyncService {

    private final SeasonRepository seasonRepository;

    /**
     * Ensures that a season exists for the requested championship year.
     *
     * @param year championship season year
     * @return synchronization summary
     */
    @Transactional
    public SeasonSyncResponseDto synchronizeSeason(Integer year) {

        validateYear(year);

        Season existingSeason =
                seasonRepository.findByYear(year)
                        .orElse(null);

        if (existingSeason != null) {

            log.info(
                    "Season already exists. Year: {}",
                    year
            );

            return SeasonSyncResponseDto.builder()
                    .year(year)
                    .created(false)
                    .existing(true)
                    .build();
        }

        Season season = new Season();

        season.setYear(year);
        season.setActive(true);

        seasonRepository.save(season);

        log.info(
                "Season synchronized successfully. Year: {}",
                year
        );

        return SeasonSyncResponseDto.builder()
                .year(year)
                .created(true)
                .existing(false)
                .build();
    }

    /**
     * Validates the requested championship year.
     */
    private void validateYear(Integer year) {

        if (year == null || year < 1950) {

            throw new IllegalArgumentException(
                    "A valid season year is required."
            );
        }
    }
}