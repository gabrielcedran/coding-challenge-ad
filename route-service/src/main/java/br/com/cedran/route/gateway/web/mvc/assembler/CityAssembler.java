package br.com.cedran.route.gateway.web.mvc.assembler;

import br.com.cedran.route.gateway.web.mvc.dto.CityResponseDTO;
import br.com.cedran.route.model.City;

public class CityAssembler {

    public static CityResponseDTO fromCityDTO(City city) {
        CityResponseDTO cityResponseDTO = new CityResponseDTO();
        cityResponseDTO.setId(city.getId());
        cityResponseDTO.setName(city.getName());
        return cityResponseDTO;
    }

}
