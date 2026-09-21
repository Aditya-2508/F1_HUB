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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenF1Client {

    private final RestClient restClient;

    @Value("${f1.api.base-url}")
    private String baseUrl;

    /*
     * OpenF1 enforces a maximum of approximately
     * 30 requests per minute.
     *
     * Rate-limit handling is intentionally kept inside
     * the integration layer so that business services
     * do not need to know about OpenF1's HTTP limitations.
     *
     * The request timestamps are maintained in a sliding
     * one-minute window.
     */
    private static final int MAX_REQUESTS_PER_MINUTE = 30;

    private static final long RATE_LIMIT_WINDOW_MILLIS = 60_000L;

    private static final int MAX_RETRIES = 3;

    private static final long INITIAL_RETRY_DELAY_MILLIS = 1_000L;

    private final Deque<Long> requestTimestamps =
            new ArrayDeque<>();

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
     *
     * A 404 response means that OpenF1 currently has
     * no results for the requested session.
     */
    public List<OpenF1SessionResultDto> getSessionResults(
            Long sessionKey) {

        try {

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

        } catch (HttpClientErrorException.NotFound exception) {

            log.info(
                    "No session results found for OpenF1 session: {}",
                    sessionKey
            );

            return List.of();
        }
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
     * Executes an OpenF1 API request while protecting
     * the application against provider rate limiting.
     *
     * Strategy:
     *
     * 1. Allow a maximum of 30 requests in any
     *    rolling 60-second window.
     *
     * 2. Wait before sending a request when the
     *    current window is full.
     *
     * 3. Retry HTTP 429 responses.
     *
     * 4. Use exponential backoff between retries.
     *
     * 5. Immediately propagate other HTTP errors.
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
                        "OpenF1 rate limit reached. "
                                + "Retrying request. Attempt {}/{}. "
                                + "Waiting {} ms.",
                        attempt,
                        MAX_RETRIES,
                        retryDelay
                );

                sleep(retryDelay);
            }
        }
    }

    /**
     * Ensures that no more than 30 requests are sent
     * during any rolling 60-second window.
     *
     * This method is synchronized because OpenF1Client
     * is a singleton Spring component and multiple
     * application threads may use the same client.
     */
    private synchronized void waitForRateLimit() {

        while (true) {

            long currentTime =
                    System.currentTimeMillis();

            /*
             * Remove requests that are older than
             * the current one-minute window.
             */
            while (!requestTimestamps.isEmpty()
                    && currentTime
                    - requestTimestamps.peekFirst()
                    >= RATE_LIMIT_WINDOW_MILLIS) {

                requestTimestamps.removeFirst();
            }

            /*
             * A new request can be sent if fewer than
             * 30 requests exist in the current window.
             */
            if (requestTimestamps.size()
                    < MAX_REQUESTS_PER_MINUTE) {

                requestTimestamps.addLast(currentTime);

                return;
            }

            /*
             * The request limit has been reached.
             * Calculate exactly how long we need to wait
             * until the oldest request leaves the window.
             */
            long oldestRequestTime =
                    requestTimestamps.peekFirst();

            long waitTime =
                    RATE_LIMIT_WINDOW_MILLIS
                            - (currentTime - oldestRequestTime);

            log.debug(
                    "OpenF1 rate limit reached. "
                            + "Waiting {} ms before next request.",
                    waitTime
            );

            sleep(waitTime);
        }
    }

    /**
     * Safely sleeps for the specified duration.
     *
     * Interrupted threads restore their interrupted status
     * and fail instead of silently ignoring interruption.
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