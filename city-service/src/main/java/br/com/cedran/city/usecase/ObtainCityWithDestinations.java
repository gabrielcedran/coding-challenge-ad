package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ObtainCityWithDestinations {

    private CityGateway cityGateway;

    public City execute(Long cityId) {
        return cityGateway.obtainByIdWithDestinations(cityId);
    }

}
