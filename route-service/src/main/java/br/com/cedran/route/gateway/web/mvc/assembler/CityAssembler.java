package br.com.cedran.route.gateway.web.mvc.assembler;

import br.com.cedran.route.gateway.web.mvc.dto.CityResponseDTO;
import br.com.cedran.route.model.City;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CityAssembler {

    public static CityResponseDTO fromCityDTO(City city) {
        CityResponseDTO cityResponseDTO = new CityResponseDTO();
        cityResponseDTO.setId(city.getId());
        cityResponseDTO.setName(city.getName());
        return cityResponseDTO;
    }

    public static List<CityResponseDTO> fromCityDTOS(List<City> city) {
        return Optional.ofNullable(city).orElse(Collections.emptyList()).stream().map(CityAssembler::fromCityDTO).collect(Collectors.toList());
    }

}
