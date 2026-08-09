package com.aditya.f1hub.service;

import com.aditya.f1hub.dto.session.SessionRequestDto;
import com.aditya.f1hub.dto.session.SessionResponseDto;

import java.util.List;

public interface SessionService {

    SessionResponseDto createSession(
            SessionRequestDto requestDto);

    List<SessionResponseDto> getAllSessions();

    SessionResponseDto getSessionById(Long id);

    SessionResponseDto updateSession(
            Long id,
            SessionRequestDto requestDto);

    void deleteSession(Long id);

    List<SessionResponseDto> searchSessions(
            String sessionName,
            String sessionType,
            Long raceId,
            Boolean active,
            Boolean cancelled);
}