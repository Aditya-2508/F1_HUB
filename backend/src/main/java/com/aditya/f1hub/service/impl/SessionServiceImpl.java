package com.aditya.f1hub.service.impl;

import com.aditya.f1hub.dto.session.SessionRequestDto;
import com.aditya.f1hub.dto.session.SessionResponseDto;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.exception.ResourceAlreadyExistsException;
import com.aditya.f1hub.exception.ResourceNotFoundException;
import com.aditya.f1hub.mapper.SessionMapper;
import com.aditya.f1hub.repository.RaceRepository;
import com.aditya.f1hub.repository.SessionRepository;
import com.aditya.f1hub.service.SessionService;
import com.aditya.f1hub.specification.SessionSpecification;
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
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final RaceRepository raceRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public SessionResponseDto createSession(
            SessionRequestDto requestDto) {

        if (sessionRepository.existsByExternalSessionId(
                requestDto.getExternalSessionId())) {

            throw new ResourceAlreadyExistsException(
                    "Session",
                    "externalSessionId",
                    requestDto.getExternalSessionId()
            );
        }

        Race race = raceRepository.findById(
                        requestDto.getRaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                requestDto.getRaceId()
                        ));

        Session session = sessionMapper.toEntity(
                requestDto,
                race
        );

        Session savedSession =
                sessionRepository.save(session);

        log.info(
                "Session created successfully: {}",
                savedSession.getSessionName()
        );

        return sessionMapper.toResponseDto(savedSession);
    }

    @Override
    public List<SessionResponseDto> getAllSessions() {

        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toResponseDto)
                .toList();
    }

    @Override
    public SessionResponseDto getSessionById(Long id) {

        Session session = sessionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                id
                        ));

        return sessionMapper.toResponseDto(session);
    }

    @Override
    @Transactional
    public SessionResponseDto updateSession(
            Long id,
            SessionRequestDto requestDto) {

        Session session = sessionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                id
                        ));

        if (!session.getExternalSessionId()
                .equals(requestDto.getExternalSessionId())
                && sessionRepository.existsByExternalSessionId(
                requestDto.getExternalSessionId())) {

            throw new ResourceAlreadyExistsException(
                    "Session",
                    "externalSessionId",
                    requestDto.getExternalSessionId()
            );
        }

        Race race = raceRepository.findById(
                        requestDto.getRaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Race",
                                "id",
                                requestDto.getRaceId()
                        ));

        sessionMapper.updateEntityFromDto(
                requestDto,
                session,
                race
        );

        Session updatedSession =
                sessionRepository.save(session);

        log.info(
                "Session updated successfully: {}",
                updatedSession.getSessionName()
        );

        return sessionMapper.toResponseDto(
                updatedSession
        );
    }

    @Override
    @Transactional
    public void deleteSession(Long id) {

        Session session = sessionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Session",
                                "id",
                                id
                        ));

        sessionRepository.delete(session);

        log.info(
                "Session deleted successfully: {}",
                session.getSessionName()
        );
    }

    @Override
    public List<SessionResponseDto> searchSessions(
            String sessionName,
            String sessionType,
            Long raceId,
            Boolean active,
            Boolean cancelled) {

        Specification<Session> specification =
                Specification.where(
                                SessionSpecification
                                        .hasSessionName(sessionName))
                        .and(
                                SessionSpecification
                                        .hasSessionType(sessionType))
                        .and(
                                SessionSpecification
                                        .hasRaceId(raceId))
                        .and(
                                SessionSpecification
                                        .hasActive(active))
                        .and(
                                SessionSpecification
                                        .hasCancelled(cancelled));

        return sessionRepository.findAll(specification)
                .stream()
                .map(sessionMapper::toResponseDto)
                .toList();
    }
}