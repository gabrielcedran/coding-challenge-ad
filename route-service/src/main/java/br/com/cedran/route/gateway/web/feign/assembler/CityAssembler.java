package br.com.cedran.route.gateway.web.feign.assembler;

import br.com.cedran.route.gateway.web.feign.dto.CityDTO;
import br.com.cedran.route.model.City;

public class CityAssembler {

    public static City fromCityDTO(CityDTO cityDTO) {
        City city = new City();
        city.setId(cityDTO.getId());
        city.setName(cityDTO.getName());
        city.setDestinations(DestinationAssembler.fromDestinationDTOS(cityDTO.getDestinations()));
        return city;
    }

}
