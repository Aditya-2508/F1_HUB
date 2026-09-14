package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Driver;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import org.springframework.stereotype.Component;

@Component
public class OpenF1DriverMapper {

    public Driver toEntity(OpenF1DriverDto dto) {

        if (dto == null) {
            return null;
        }

        Driver driver = new Driver();

        driver.setExternalDriverId(generateExternalId(dto));

        driver.setDriverNumber(dto.getDriverNumber());

        String[] nameParts = resolveDriverName(dto);

        driver.setFirstName(nameParts[0]);

        driver.setLastName(nameParts[1]);

        driver.setFullName(dto.getFullName());

        driver.setAbbreviation(dto.getAbbreviation());

        driver.setNationality(convertCountryCode(dto.getCountryCode()));

        driver.setProfileImageUrl(dto.getHeadshotUrl());

        driver.setActive(true);

        return driver;
    }

    /**
     * Resolves the driver's first and last name.
     *
     * OpenF1 may omit first_name and last_name for some historical
     * driver records while still providing full_name.
     */
    private String[] resolveDriverName(OpenF1DriverDto dto) {

        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();

        if (isNotBlank(firstName) && isNotBlank(lastName)) {
            return new String[]{firstName, lastName};
        }

        String fullName = dto.getFullName();

        if (isNotBlank(fullName)) {

            String trimmedFullName = fullName.trim();

            int lastSpaceIndex = trimmedFullName.lastIndexOf(' ');

            if (lastSpaceIndex > 0 && lastSpaceIndex < trimmedFullName.length() - 1) {

                String derivedFirstName =
                        trimmedFullName.substring(0, lastSpaceIndex).trim();

                String derivedLastName =
                        trimmedFullName.substring(lastSpaceIndex + 1).trim();

                if (!isNotBlank(firstName)) {
                    firstName = derivedFirstName;
                }

                if (!isNotBlank(lastName)) {
                    lastName = derivedLastName;
                }
            }
        }

        return new String[]{firstName, lastName};
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Generates a stable identifier for our application.
     */
    private String generateExternalId(OpenF1DriverDto dto) {

        if (dto.getAbbreviation() != null) {
            return dto.getAbbreviation().toLowerCase();
        }

        return dto.getFullName()
                .toLowerCase()
                .replace(" ", "_");
    }

    /**
     * Converts ISO country code to readable nationality.
     * This will be improved later with a lookup table.
     */
    private String convertCountryCode(String countryCode) {

        if (countryCode == null) {
            return null;
        }

        return switch (countryCode.toUpperCase()) {

            case "NED" -> "Dutch";
            case "GBR" -> "British";
            case "ESP" -> "Spanish";
            case "MON" -> "Monégasque";
            case "FRA" -> "French";
            case "GER" -> "German";
            case "AUS" -> "Australian";
            case "MEX" -> "Mexican";
            case "CAN" -> "Canadian";
            case "JPN" -> "Japanese";
            case "THA" -> "Thai";
            case "FIN" -> "Finnish";
            case "ITA" -> "Italian";
            case "BRA" -> "Brazilian";
            case "CHN" -> "Chinese";

            default -> countryCode;
        };
    }
}