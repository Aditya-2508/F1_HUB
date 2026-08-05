package com.aditya.f1hub.integration.client;

import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
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

    public List<OpenF1DriverDto> getDrivers() {

        return restClient
                .get()
                .uri(baseUrl + "/drivers")
                .retrieve()
                .body(new ParameterizedTypeReference<List<OpenF1DriverDto>>() {});
    }


    public List<OpenF1CircuitDto> getCircuits() {

        return restClient.get()
                .uri("/meetings")
                .retrieve()
                .body(new ParameterizedTypeReference<List<OpenF1CircuitDto>>() {});
    }
}