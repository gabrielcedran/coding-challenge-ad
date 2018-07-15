package br.com.cedran.route.gateway.web;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.gateway.web.feign.CityClient;
import br.com.cedran.route.gateway.web.feign.assembler.CityAssembler;
import br.com.cedran.route.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CityWebGateway implements CityGateway {

    private CityClient cityClient;

    @Override
    public City obtainWithDestinationsById(Long cityId) {
        return CityAssembler.fromCityDTO(cityClient.byIdWithDestinations(cityId));
    }
}
