package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.usecase.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ObtainCityWithDestinations {

    private CityGateway cityGateway;

    public City execute(Long cityId) {
        return Optional.ofNullable(cityGateway.obtainByIdWithDestinations(cityId)).orElseThrow(NotFoundException::new);
    }

}
