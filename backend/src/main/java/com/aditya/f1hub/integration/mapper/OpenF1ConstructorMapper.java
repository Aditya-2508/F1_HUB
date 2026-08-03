package com.aditya.f1hub.integration.mapper;

import com.aditya.f1hub.entity.Constructor;
import com.aditya.f1hub.integration.dto.OpenF1DriverDto;
import org.springframework.stereotype.Component;

@Component
public class OpenF1ConstructorMapper {

    public Constructor toEntity(OpenF1DriverDto dto) {

        Constructor constructor = new Constructor();

        constructor.setExternalConstructorId(generateExternalId(dto));

        constructor.setName(dto.getTeamName());

        constructor.setFullName(dto.getTeamName());

        constructor.setCountryCode(dto.getCountryCode());

        constructor.setTeamColour(dto.getTeamColour());

        constructor.setLogoUrl(null);

        constructor.setNationality(dto.getCountryCode());

        constructor.setActive(true);

        return constructor;
    }

    private String generateExternalId(OpenF1DriverDto dto) {

        return dto.getTeamName()
                .toLowerCase()
                .replace(" ", "_")
                .replace("-", "_");
    }

}