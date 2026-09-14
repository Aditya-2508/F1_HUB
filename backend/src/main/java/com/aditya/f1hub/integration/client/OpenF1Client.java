package com.aditya.f1hub.integration.client;

import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import com.aditya.f1hub.integration.dto.OpenF1LapDto;
import com.aditya.f1hub.integration.dto.OpenF1RaceDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionDto;
import com.aditya.f1hub.integration.dto.OpenF1SessionResultDto;
import com.aditya.f1hub.integration.dto.OpenF1StartingGridDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenF1Client {

    private final RestClient restClient;

    @Value("${f1.api.base-url}")
    private String baseUrl;

    /*
     * OpenF1 allows a maximum of approximately 3 requests per second.
     *
     * We therefore keep a small delay between requests and also retry
     * requests when OpenF1 explicitly responds with HTTP 429.
     *
     * This keeps rate-limit handling inside the integration layer instead
     * of leaking external API concerns into business services.
     */
    private static final long MIN_REQUEST_INTERVAL_MILLIS = 350L;

    private static final int MAX_RETRIES = 3;

    private static final long INITIAL_RETRY_DELAY_MILLIS = 1000L;

    private long lastRequestTime = 0L;

    /**
     * Fetches all drivers from the OpenF1 API.
     */
    public List<OpenF1DriverDto> getDrivers() {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(baseUrl + "/drivers")
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1DriverDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches all circuits from the OpenF1 API.
     *
     * OpenF1 exposes circuit information through the meetings endpoint.
     */
    public List<OpenF1CircuitDto> getCircuits() {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(baseUrl + "/meetings")
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1CircuitDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches race meetings for a specific season year
     * from the OpenF1 API.
     */
    public List<OpenF1RaceDto> getRaces(Integer year) {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(baseUrl + "/meetings?year=" + year)
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1RaceDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches sessions for a specific season year
     * from the OpenF1 API.
     */
    public List<OpenF1SessionDto> getSessions(Integer year) {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(baseUrl + "/sessions?year=" + year)
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1SessionDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches driver results for a specific session
     * from the OpenF1 API.
     */
    public List<OpenF1SessionResultDto> getSessionResults(
            Long sessionKey) {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(
                                baseUrl
                                        + "/session_result?session_key="
                                        + sessionKey
                        )
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1SessionResultDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches the starting grid for a specific session
     * from the OpenF1 API.
     *
     * Starting grid data is optional. OpenF1 may return
     * 404 when no starting grid is available for a session.
     * In that case, an empty list is returned.
     */
    public List<OpenF1StartingGridDto> getStartingGrid(
            Long sessionKey) {

        try {

            return executeWithRetry(
                    () -> restClient
                            .get()
                            .uri(
                                    baseUrl
                                            + "/starting_grid?session_key="
                                            + sessionKey
                            )
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            List<OpenF1StartingGridDto>>() {
                                    }
                            )
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

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(
                                baseUrl
                                        + "/drivers?session_key="
                                        + sessionKey
                        )
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1DriverDto>>() {
                                }
                        )
        );
    }

    /**
     * Fetches lap data for a specific session
     * from the OpenF1 API.
     */
    public List<OpenF1LapDto> getLaps(Long sessionKey) {

        return executeWithRetry(
                () -> restClient
                        .get()
                        .uri(
                                baseUrl
                                        + "/laps?session_key="
                                        + sessionKey
                        )
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<OpenF1LapDto>>() {
                                }
                        )
        );
    }

    /**
     * Executes an OpenF1 API request while protecting the application
     * against provider rate limiting.
     *
     * Strategy:
     *
     * 1. Keep a minimum interval between outgoing requests.
     * 2. Retry HTTP 429 responses.
     * 3. Use exponential backoff between retries.
     * 4. Immediately propagate other HTTP errors.
     */
    private <T> T executeWithRetry(
            OpenF1Request<T> request) {

        int attempt = 0;

        while (true) {

            try {

                waitForRateLimit();

                return request.execute();

            } catch (HttpClientErrorException.TooManyRequests exception) {

                if (attempt >= MAX_RETRIES) {

                    log.error(
                            "OpenF1 rate limit exceeded after {} retries.",
                            MAX_RETRIES,
                            exception
                    );

                    throw exception;
                }

                attempt++;

                long retryDelay =
                        INITIAL_RETRY_DELAY_MILLIS
                                * (1L << (attempt - 1));

                log.warn(
                        "OpenF1 rate limit reached. " +
                                "Retrying request. Attempt {}/{}. " +
                                "Waiting {} ms.",
                        attempt,
                        MAX_RETRIES,
                        retryDelay
                );

                sleep(retryDelay);
            }
        }
    }

    /**
     * Ensures that requests are not sent faster than the configured
     * minimum interval.
     *
     * This is intentionally synchronized because multiple application
     * threads could access the same OpenF1Client instance.
     */
    private synchronized void waitForRateLimit() {

        long currentTime = System.currentTimeMillis();

        long elapsedTime =
                currentTime - lastRequestTime;

        long remainingDelay =
                MIN_REQUEST_INTERVAL_MILLIS - elapsedTime;

        if (remainingDelay > 0) {

            sleep(remainingDelay);
        }

        lastRequestTime = System.currentTimeMillis();
    }

    /**
     * Safely sleeps for the specified duration.
     *
     * Interrupted threads restore their interrupted status and fail
     * instead of silently ignoring interruption.
     */
    private void sleep(long milliseconds) {

        try {

            Thread.sleep(milliseconds);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "OpenF1 request was interrupted.",
                    exception
            );
        }
    }

    /**
     * Functional abstraction representing an OpenF1 request.
     */
    @FunctionalInterface
    private interface OpenF1Request<T> {

        T execute();
    }
}