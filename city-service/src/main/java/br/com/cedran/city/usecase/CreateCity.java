package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.cedran.city.model.BusinessError.CITY_NAME_EXISTENT;

@Service
@AllArgsConstructor
public class CreateCity {

    private CityGateway cityGateway;

    public City execute(String name) {
        Optional.ofNullable(cityGateway.obtainByName(name))
                .ifPresent((city) -> {throw new BusinessException(CITY_NAME_EXISTENT);});

        City city = new City();
        city.setName(name);

        return cityGateway.create(city);
    }

}
