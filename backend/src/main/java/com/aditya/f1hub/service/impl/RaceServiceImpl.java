package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.race.RaceRequestDto;
import com.aditya.f1hub.dto.race.RaceResponseDto;
import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Season;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.mapper.RaceMapper;
import com.aditya.f1hub.repository.CircuitRepository;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SeasonRepository;
import com.aditya.f1hub.service.RaceService;
import com.aditya.f1hub.specification.RaceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final SeasonRepository seasonRepository;
    private final CircuitRepository circuitRepository;
    private final RaceMapper raceMapper;

    @Override
    @Transactional
    public RaceResponseDto createRace(RaceRequestDto requestDto) {

        if (raceRepository.existsByExternalMeetingId(
                requestDto.getExternalMeetingId())) {

            throw new ResourceAlreadyExistsException(
                    "Race",
                    "externalMeetingId",
                    requestDto.getExternalMeetingId()
            );
        }

        Season season = seasonRepository.findById(
                        requestDto.getSeasonId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Season",
                                "id",
                                requestDto.getSeasonId()
                        ));

        Circuit circuit = circuitRepository.findById(
                        requestDto.getCircuitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Circuit",
                                "id",
                                requestDto.getCircuitId()
                        ));

        Race race = raceMapper.toEntity(
                requestDto,
                season,
                circuit
        );

        Race savedRace = raceRepository.save(race);

        log.info(
                "Race created successfully: {}",
                savedRace.getName()
        );

        return raceMapper.toResponseDto(savedRace);
    }

    @Override
    public List<RaceResponseDto> getAllRaces() {

        return raceRepository.findAll()
                .stream()
                .map(raceMapper::toResponseDto)
                .toList();
    }

    @Override
    public RaceResponseDto getRaceById(Long id) {

        Race race = raceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                id
                        ));

        return raceMapper.toResponseDto(race);
    }

    @Override
    @Transactional
    public RaceResponseDto updateRace(
            Long id,
            RaceRequestDto requestDto) {

        Race race = raceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                id
                        ));

        if (!race.getExternalMeetingId()
                .equals(requestDto.getExternalMeetingId())
                && raceRepository.existsByExternalMeetingId(
                requestDto.getExternalMeetingId())) {

            throw new ResourceAlreadyExistsException(
                    "Race",
                    "externalMeetingId",
                    requestDto.getExternalMeetingId()
            );
        }

        Season season = seasonRepository.findById(
                        requestDto.getSeasonId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Season",
                                "id",
                                requestDto.getSeasonId()
                        ));

        Circuit circuit = circuitRepository.findById(
                        requestDto.getCircuitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Circuit",
                                "id",
                                requestDto.getCircuitId()
                        ));

        raceMapper.updateEntityFromDto(
                requestDto,
                race,
                season,
                circuit
        );

        Race updatedRace = raceRepository.save(race);

        log.info(
                "Race updated successfully: {}",
                updatedRace.getName()
        );

        return raceMapper.toResponseDto(updatedRace);
    }

    @Override
    @Transactional
    public void deleteRace(Long id) {

        Race race = raceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                id
                        ));

        raceRepository.delete(race);

        log.info(
                "Race deleted successfully: {}",
                race.getName()
        );
    }

    @Override
    public List<RaceResponseDto> searchRaces(
            String name,
            Long seasonId,
            Long circuitId,
            String countryName,
            Boolean active,
            Boolean cancelled) {

        Specification<Race> specification =
                Specification.where(
                                RaceSpecification.hasName(name))
                        .and(RaceSpecification.hasSeasonId(seasonId))
                        .and(RaceSpecification.hasCircuitId(circuitId))
                        .and(RaceSpecification.hasCountryName(countryName))
                        .and(RaceSpecification.hasActive(active))
                        .and(RaceSpecification.hasCancelled(cancelled));

        return raceRepository.findAll(specification)
                .stream()
                .map(raceMapper::toResponseDto)
                .toList();
    }
}