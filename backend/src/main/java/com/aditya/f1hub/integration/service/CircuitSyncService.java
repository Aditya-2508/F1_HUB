package com.aditya.f1hub.integration.service;

import com.aditya.f1hub.entity.Circuit;
import com.aditya.f1hub.integration.client.OpenF1Client;
import com.aditya.f1hub.integration.dto.CircuitSyncResponseDto;
import com.aditya.f1hub.integration.dto.OpenF1CircuitDto;
import com.aditya.f1hub.integration.mapper.OpenF1CircuitMapper;
import com.aditya.f1hub.repository.CircuitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitSyncService {

    private final OpenF1Client openF1Client;
    private final CircuitRepository circuitRepository;
    private final OpenF1CircuitMapper circuitMapper;

    @Transactional
    public CircuitSyncResponseDto synchronizeCircuits() {

        List<OpenF1CircuitDto> circuitDtos =
                openF1Client.getCircuits();

        int totalFetched = circuitDtos.size();
        int newCircuits = 0;
        int existingCircuits = 0;
        int failed = 0;

        Set<String> processedCircuitIds = new HashSet<>();

        for (OpenF1CircuitDto dto : circuitDtos) {

            try {

                if (dto.getCircuitKey() == null) {
                    failed++;

                    log.warn(
                            "Skipping circuit because circuit_key is missing."
                    );

                    continue;
                }

                String externalCircuitId =
                        String.valueOf(dto.getCircuitKey());

                /*
                 * The meetings endpoint can contain the same
                 * circuit across multiple Grand Prix seasons.
                 *
                 * Prevent duplicate processing within
                 * the current API response.
                 */
                if (!processedCircuitIds.add(externalCircuitId)) {
                    continue;
                }

                /*
                 * Check whether the circuit already exists
                 * in our database.
                 */
                if (circuitRepository.existsByExternalCircuitId(
                        externalCircuitId)) {

                    existingCircuits++;
                    continue;
                }

                Circuit circuit = circuitMapper.toEntity(dto);

                circuitRepository.save(circuit);

                newCircuits++;

                log.info(
                        "Circuit synchronized successfully: {}",
                        circuit.getCircuitName()
                );

            } catch (Exception exception) {

                failed++;

                log.error(
                        "Failed to synchronize circuit: {}",
                        dto.getCircuitShortName(),
                        exception
                );
            }
        }

        log.info(
                "Circuit synchronization completed. " +
                        "Total: {}, New: {}, Existing: {}, Failed: {}",
                totalFetched,
                newCircuits,
                existingCircuits,
                failed
        );

        return CircuitSyncResponseDto.builder()
                .totalFetched(totalFetched)
                .newCircuits(newCircuits)
                .existingCircuits(existingCircuits)
                .failed(failed)
                .build();
    }
}