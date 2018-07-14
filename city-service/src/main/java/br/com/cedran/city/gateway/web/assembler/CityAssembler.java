package br.com.cedran.city.gateway.web.assembler;

import br.com.cedran.city.gateway.web.dtos.CityRequestDTO;
import br.com.cedran.city.gateway.web.dtos.CityResponseDTO;
import br.com.cedran.city.model.City;

public class CityAssembler {

    public static City fromCityRequestDTO(Long id, CityRequestDTO cityRequestDTO) {
        City city = new City();
        city.setId(id);
        city.setName(cityRequestDTO.getName());
        return city;
    }

    public static CityResponseDTO fromCity(City city, boolean destinations) {
        CityResponseDTO cityResponseDTO = new CityResponseDTO();
        cityResponseDTO.setId(city.getId());
        cityResponseDTO.setName(city.getName());
        if (destinations) {
            cityResponseDTO.setDestinations(DestinationAssembler.fromDestination(city.getDestinations()));
        }
        return cityResponseDTO;
    }

}
