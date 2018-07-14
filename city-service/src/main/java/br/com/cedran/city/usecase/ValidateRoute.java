package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.cedran.city.model.BusinessError.CITY_NOT_EXISTENT;
import static br.com.cedran.city.model.BusinessError.ROUTE_WITH_DUPLICATED_CITY;

@AllArgsConstructor
@Service
public class ValidateRoute {

    private CityGateway cityGateway;

    public void execute(List<City> route) {

        List<Long> citiesIds = route.stream().map(City::getId).distinct().collect(Collectors.toList());
        if (citiesIds.size() < route.size()) {
            throw new BusinessException(ROUTE_WITH_DUPLICATED_CITY);
        }

        List<City> cities = cityGateway.obtainByIds(citiesIds);
        if (cities.size() < route.size()) {
            // FIXME provide list of nonexistent cities
            throw new BusinessException(CITY_NOT_EXISTENT);
        }
    }

}
