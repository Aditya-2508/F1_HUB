package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Session;
import com.aditya.f1hub.integration.dto.OpenF1SessionDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class OpenF1SessionMapper {

    /**
     * Converts OpenF1 Session DTO to Session Entity.
     *
     * @param dto  OpenF1 session data
     * @param race resolved Race entity
     * @return mapped Session entity
     */
    public Session toEntity(
            OpenF1SessionDto dto,
            Race race) {

        if (dto == null) {
            return null;
        }

        return Session.builder()
                .externalSessionId(
                        dto.getSessionKey() != null
                                ? String.valueOf(dto.getSessionKey())
                                : null
                )
                .sessionName(dto.getSessionName())
                .sessionType(dto.getSessionType())
                .startTime(parseDateTime(dto.getDateStart()))
                .endTime(parseDateTime(dto.getDateEnd()))
                .cancelled(
                        dto.getCancelled() != null
                                ? dto.getCancelled()
                                : false
                )
                .active(true)
                .race(race)
                .build();
    }

    /**
     * Updates an existing Session entity using OpenF1 data.
     *
     * Internal application-owned fields such as active
     * are intentionally preserved.
     */
    public void updateEntityFromDto(
            OpenF1SessionDto dto,
            Session session,
            Race race) {

        if (dto == null || session == null) {
            return;
        }

        session.setExternalSessionId(
                dto.getSessionKey() != null
                        ? String.valueOf(dto.getSessionKey())
                        : null
        );

        session.setSessionName(dto.getSessionName());
        session.setSessionType(dto.getSessionType());
        session.setStartTime(
                parseDateTime(dto.getDateStart())
        );
        session.setEndTime(
                parseDateTime(dto.getDateEnd())
        );
        session.setCancelled(
                dto.getCancelled() != null
                        ? dto.getCancelled()
                        : false
        );
        session.setRace(race);
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