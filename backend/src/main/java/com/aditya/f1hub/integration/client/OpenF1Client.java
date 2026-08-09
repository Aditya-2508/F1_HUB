package com.aditya.f1hub.integration.client;

import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.dto.OpenF1RaceDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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
}