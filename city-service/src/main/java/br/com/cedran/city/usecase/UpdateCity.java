package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.cedran.city.model.ErrorCode.CITY_NOT_EXISTENT;
import static br.com.cedran.city.model.ErrorCode.CITY_NAME_EXISTENT;

@Service
@AllArgsConstructor
public class UpdateCity {

    private CityGateway cityGateway;

    public City execute(City city) {
        // FIXME add proper message
        Optional.ofNullable(cityGateway.obtainByName(city.getName()))
                .filter(foundCity -> !foundCity.getId().equals(city.getId()))
                .ifPresent(foundCity -> {throw new BusinessException(CITY_NAME_EXISTENT);});

        // FIXME add proper message
        City foundCity = Optional.ofNullable(cityGateway.obtainById(city.getId()))
                .orElseThrow(() -> new BusinessException(CITY_NOT_EXISTENT));

        return cityGateway.update(city);
    }

}
