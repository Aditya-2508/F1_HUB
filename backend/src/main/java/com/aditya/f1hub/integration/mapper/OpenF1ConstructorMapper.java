package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import org.springframework.stereotype.Component;

@Component
public class OpenF1ConstructorMapper {

    public Constructor toEntity(OpenF1DriverDto dto) {

        Constructor constructor = new Constructor();

        constructor.setExternalConstructorId(
                generateExternalId(dto)
        );

        constructor.setName(dto.getTeamName());

        constructor.setFullName(dto.getTeamName());

        constructor.setCountryCode(dto.getCountryCode());

        constructor.setTeamColour(dto.getTeamColour());

        constructor.setLogoUrl(null);

        constructor.setNationality(
                resolveNationality(dto)
        );

        constructor.setActive(true);

        return constructor;
    }

    /**
     * Resolves an OpenF1 team name to the canonical
     * F1Hub external constructor ID.
     *
     * This is used when resolving an already existing
     * Constructor during Result synchronization.
     */
    public String resolveExternalConstructorId(
            String teamName) {

        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException(
                    "Team name cannot be null or blank."
            );
        }

        String normalizedTeamName =
                teamName
                        .trim()
                        .toLowerCase();

        return switch (normalizedTeamName) {

            case "racing bulls" -> "rb";

            default -> normalizedTeamName
                    .replace(" ", "_")
                    .replace("-", "_");
        };
    }

    /**
     * Resolves the constructor nationality.
     *
     * OpenF1 provides country_code as part of the
     * driver information, but this value may be null
     * for some constructors.
     *
     * Known constructor-specific fallbacks are therefore
     * provided for teams where the OpenF1 country code
     * is unavailable.
     */
    private String resolveNationality(
            OpenF1DriverDto dto) {

        if (dto.getCountryCode() != null
                && !dto.getCountryCode().isBlank()) {

            return resolveCountryName(
                    dto.getCountryCode()
            );
        }

        if (dto.getTeamName() == null
                || dto.getTeamName().isBlank()) {

            return null;
        }

        return switch (
                dto.getTeamName()
                        .trim()
                        .toLowerCase()
                ) {

            case "audi" -> "German";

            case "cadillac" -> "American";

            default -> null;
        };
    }

    /**
     * Converts commonly used country codes into
     * human-readable nationality values.
     */
    private String resolveCountryName(
            String countryCode) {

        return switch (
                countryCode
                        .trim()
                        .toUpperCase()
                ) {

            case "AT" -> "Austrian";
            case "AU" -> "Australian";
            case "BE" -> "Belgian";
            case "BR" -> "Brazilian";
            case "CA" -> "Canadian";
            case "CH" -> "Swiss";
            case "DE" -> "German";
            case "DK" -> "Danish";
            case "ES" -> "Spanish";
            case "FI" -> "Finnish";
            case "FR" -> "French";
            case "GB" -> "British";
            case "IT" -> "Italian";
            case "JP" -> "Japanese";
            case "MX" -> "Mexican";
            case "NL" -> "Dutch";
            case "NZ" -> "New Zealander";
            case "PL" -> "Polish";
            case "PT" -> "Portuguese";
            case "SE" -> "Swedish";
            case "TH" -> "Thai";
            case "US" -> "American";

            default -> countryCode.trim();
        };
    }

    private String generateExternalId(
            OpenF1DriverDto dto) {

        return resolveExternalConstructorId(
                dto.getTeamName()
        );
    }
}