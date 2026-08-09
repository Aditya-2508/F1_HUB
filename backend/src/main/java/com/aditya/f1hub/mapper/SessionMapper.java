package com.aditya.f1hub.mapper;

import com.aditya.f1hub.dto.session.SessionRequestDto;
import com.aditya.f1hub.dto.session.SessionResponseDto;
import com.aditya.f1hub.entity.Race;
import com.aditya.f1hub.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    /**
     * Converts SessionRequestDto to Session Entity.
     *
     * @param requestDto request data
     * @param race       resolved Race entity
     * @return mapped Session entity
     */
    public Session toEntity(
            SessionRequestDto requestDto,
            Race race) {

        if (requestDto == null) {
            return null;
        }

        return Session.builder()
                .externalSessionId(requestDto.getExternalSessionId())
                .sessionName(requestDto.getSessionName())
                .sessionType(requestDto.getSessionType())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .cancelled(requestDto.getCancelled())
                .active(requestDto.getActive())
                .race(race)
                .build();
    }

    /**
     * Updates an existing Session entity from SessionRequestDto.
     *
     * @param requestDto request data
     * @param session    existing Session entity
     * @param race       resolved Race entity
     */
    public void updateEntityFromDto(
            SessionRequestDto requestDto,
            Session session,
            Race race) {

        if (requestDto == null || session == null) {
            return;
        }

        session.setExternalSessionId(
                requestDto.getExternalSessionId()
        );
        session.setSessionName(
                requestDto.getSessionName()
        );
        session.setSessionType(
                requestDto.getSessionType()
        );
        session.setStartTime(
                requestDto.getStartTime()
        );
        session.setEndTime(
                requestDto.getEndTime()
        );
        session.setCancelled(
                requestDto.getCancelled()
        );
        session.setActive(
                requestDto.getActive()
        );
        session.setRace(race);
    }

    /**
     * Converts Session Entity to SessionResponseDto.
     *
     * @param session Session entity
     * @return mapped response DTO
     */
    public SessionResponseDto toResponseDto(Session session) {

        if (session == null) {
            return null;
        }

        return SessionResponseDto.builder()
                .id(session.getId())
                .externalSessionId(
                        session.getExternalSessionId()
                )
                .sessionName(
                        session.getSessionName()
                )
                .sessionType(
                        session.getSessionType()
                )
                .startTime(
                        session.getStartTime()
                )
                .endTime(
                        session.getEndTime()
                )
                .cancelled(
                        session.getCancelled()
                )
                .active(
                        session.getActive()
                )
                .raceId(
                        session.getRace() != null
                                ? session.getRace().getId()
                                : null
                )
                .createdAt(
                        session.getCreatedAt()
                )
                .updatedAt(
                        session.getUpdatedAt()
                )
                .build();
    }
}