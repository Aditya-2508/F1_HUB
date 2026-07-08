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

        driver.setFirstName(dto.getFirstName());

        driver.setLastName(dto.getLastName());

        driver.setFullName(dto.getFullName());

        driver.setAbbreviation(dto.getAbbreviation());

        driver.setNationality(convertCountryCode(dto.getCountryCode()));

        driver.setProfileImageUrl(dto.getHeadshotUrl());

        driver.setActive(true);

        return driver;
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