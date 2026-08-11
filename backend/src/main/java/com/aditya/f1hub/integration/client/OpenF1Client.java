package com.aditya.f1hub.integration.client;

import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.dto.OpenF1RaceDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import com.aditya.f1hub.integration.dto.OpenF1StartingGridDto;
import com.aditya.f1hub.integration.dto.OpenF1LapDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenF1Client {

    private final RestClient restClient;

    @Value("${f1.api.base-url}")
    private String baseUrl;

    /**
     * Fetches all drivers from the OpenF1 API.
     */
    public List<OpenF1DriverDto> getDrivers() {

        return restClient
                .get()
                .uri(baseUrl + "/drivers")
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1DriverDto>>() {}
                );
    }

    /**
     * Fetches all circuits from the OpenF1 API.
     */
    public List<OpenF1CircuitDto> getCircuits() {

        return restClient
                .get()
                .uri(baseUrl + "/meetings")
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1CircuitDto>>() {}
                );
    }

    /**
     * Fetches race meetings for a specific season year
     * from the OpenF1 API.
     */
    public List<OpenF1RaceDto> getRaces(Integer year) {

        return restClient
                .get()
                .uri(baseUrl + "/meetings?year=" + year)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1RaceDto>>() {}
                );
    }

    /**
     * Fetches sessions for a specific season year
     * from the OpenF1 API.
     */
    public List<OpenF1SessionDto> getSessions(Integer year) {

        return restClient
                .get()
                .uri(baseUrl + "/sessions?year=" + year)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1SessionDto>>() {}
                );
    }

    /**
     * Fetches driver results for a specific session
     * from the OpenF1 API.
     */

    public List<OpenF1SessionResultDto> getSessionResults(Long sessionKey) {

        return restClient
                .get()
                .uri(baseUrl + "/session_result?session_key=" + sessionKey)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1SessionResultDto>>() {}
                );
    }

    /**
     * Fetches the starting grid for a specific session
     * from the OpenF1 API.
     */
    /**
     * Fetches starting grid data for a specific session
     * from the OpenF1 API.
     *
     * Starting grid data is optional. OpenF1 may return
     * 404 when no starting grid is available for a session.
     * In that case, an empty list is returned so that result
     * synchronization can continue.
     */
    public List<OpenF1StartingGridDto> getStartingGrid(
            Long sessionKey) {

        try {

            return restClient
                    .get()
                    .uri(
                            baseUrl
                                    + "/starting_grid?session_key="
                                    + sessionKey
                    )
                    .retrieve()
                    .body(
                            new ParameterizedTypeReference<
                                    List<OpenF1StartingGridDto>>() {}
                    );

        } catch (HttpClientErrorException.NotFound exception) {

            log.warn(
                    "No starting grid data found for OpenF1 session: {}",
                    sessionKey
            );

            return List.of();
        }
    }

    /**
     * Fetches drivers participating in a specific session
     * from the OpenF1 API.
     */
    public List<OpenF1DriverDto> getDrivers(Long sessionKey) {

        return restClient
                .get()
                .uri(baseUrl + "/drivers?session_key=" + sessionKey)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1DriverDto>>() {}
                );
    }

    /**
     * Fetches lap data for a specific session
     * from the OpenF1 API.
     */
    public List<OpenF1LapDto> getLaps(Long sessionKey) {

        return restClient
                .get()
                .uri(baseUrl + "/laps?session_key=" + sessionKey)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<OpenF1LapDto>>() {}
                );
    }
}