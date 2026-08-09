package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.race.RaceRequestDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;
import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Season;
import org.springframework.stereotype.Component;

@Component
public class RaceMapper {

    /**
     * Converts RaceRequestDto to Race Entity
     */
    public Race toEntity(
            RaceRequestDto requestDto,
            Season season,
            Circuit circuit) {

        if (requestDto == null) {
            return null;
        }

        return Race.builder()
                .externalMeetingId(requestDto.getExternalMeetingId())
                .name(requestDto.getName())
                .officialName(requestDto.getOfficialName())
                .roundNumber(requestDto.getRoundNumber())
                .weekendStart(requestDto.getWeekendStart())
                .weekendEnd(requestDto.getWeekendEnd())
                .location(requestDto.getLocation())
                .countryName(requestDto.getCountryName())
                .countryCode(requestDto.getCountryCode())
                .gmtOffset(requestDto.getGmtOffset())
                .cancelled(requestDto.getCancelled())
                .active(requestDto.getActive())
                .season(season)
                .circuit(circuit)
                .build();
    }

    /**
     * Updates an existing Race entity from RaceRequestDto
     */
    public void updateEntityFromDto(
            RaceRequestDto requestDto,
            Race race,
            Season season,
            Circuit circuit) {

        if (requestDto == null || race == null) {
            return;
        }

        race.setExternalMeetingId(requestDto.getExternalMeetingId());
        race.setName(requestDto.getName());
        race.setOfficialName(requestDto.getOfficialName());
        race.setRoundNumber(requestDto.getRoundNumber());
        race.setWeekendStart(requestDto.getWeekendStart());
        race.setWeekendEnd(requestDto.getWeekendEnd());
        race.setLocation(requestDto.getLocation());
        race.setCountryName(requestDto.getCountryName());
        race.setCountryCode(requestDto.getCountryCode());
        race.setGmtOffset(requestDto.getGmtOffset());
        race.setCancelled(requestDto.getCancelled());
        race.setActive(requestDto.getActive());
        race.setSeason(season);
        race.setCircuit(circuit);
    }

    /**
     * Converts Race Entity to RaceResponseDto
     */
    public RaceResponseDto toResponseDto(Race race) {

        if (race == null) {
            return null;
        }

        return RaceResponseDto.builder()
                .id(race.getId())
                .externalMeetingId(race.getExternalMeetingId())
                .name(race.getName())
                .officialName(race.getOfficialName())
                .roundNumber(race.getRoundNumber())
                .weekendStart(race.getWeekendStart())
                .weekendEnd(race.getWeekendEnd())
                .location(race.getLocation())
                .countryName(race.getCountryName())
                .countryCode(race.getCountryCode())
                .gmtOffset(race.getGmtOffset())
                .cancelled(race.getCancelled())
                .active(race.getActive())
                .seasonId(
                        race.getSeason() != null
                                ? race.getSeason().getId()
                                : null
                )
                .circuitId(
                        race.getCircuit() != null
                                ? race.getCircuit().getId()
                                : null
                )
                .createdAt(race.getCreatedAt())
                .updatedAt(race.getUpdatedAt())
                .build();
    }
}