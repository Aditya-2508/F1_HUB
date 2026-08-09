package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.integration.dto.OpenF1RaceDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class OpenF1RaceMapper {

    /**
     * Converts OpenF1 Race DTO to Race Entity.
     *
     * @param dto     OpenF1 race data
     * @param season  resolved Season entity
     * @param circuit resolved Circuit entity
     * @return mapped Race entity
     */
    public Race toEntity(
            OpenF1RaceDto dto,
            Season season,
            Circuit circuit) {

        if (dto == null) {
            return null;
        }

        return Race.builder()
                .externalMeetingId(dto.getMeetingKey())
                .name(dto.getMeetingName())
                .officialName(dto.getMeetingOfficialName())
                .roundNumber(null)
                .weekendStart(parseDateTime(dto.getDateStart()))
                .weekendEnd(parseDateTime(dto.getDateEnd()))
                .location(dto.getLocation())
                .countryName(dto.getCountryName())
                .countryCode(dto.getCountryCode())
                .gmtOffset(dto.getGmtOffset())
                .cancelled(
                        dto.getCancelled() != null
                                ? dto.getCancelled()
                                : false
                )
                .active(true)
                .season(season)
                .circuit(circuit)
                .build();
    }

    /**
     * Updates an existing Race entity using OpenF1 data.
     *
     * Internal application-owned fields such as active and roundNumber
     * are intentionally preserved.
     */
    public void updateEntityFromDto(
            OpenF1RaceDto dto,
            Race race,
            Season season,
            Circuit circuit) {

        if (dto == null || race == null) {
            return;
        }

        race.setExternalMeetingId(dto.getMeetingKey());
        race.setName(dto.getMeetingName());
        race.setOfficialName(dto.getMeetingOfficialName());
        race.setWeekendStart(parseDateTime(dto.getDateStart()));
        race.setWeekendEnd(parseDateTime(dto.getDateEnd()));
        race.setLocation(dto.getLocation());
        race.setCountryName(dto.getCountryName());
        race.setCountryCode(dto.getCountryCode());
        race.setGmtOffset(dto.getGmtOffset());
        race.setCancelled(
                dto.getCancelled() != null
                        ? dto.getCancelled()
                        : false
        );
        race.setSeason(season);
        race.setCircuit(circuit);
    }

    /**
     * Converts an OpenF1 timestamp to LocalDateTime.
     */
    private LocalDateTime parseDateTime(String dateTime) {

        if (dateTime == null || dateTime.isBlank()) {
            return null;
        }

        return OffsetDateTime.parse(dateTime)
                .toLocalDateTime();
    }
}