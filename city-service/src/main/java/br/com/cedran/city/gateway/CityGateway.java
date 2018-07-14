package br.com.cedran.city.gateway;

import br.com.cedran.city.model.City;

public interface CityGateway {
    City obtainByName(String name);
    City obtainById(Long id);
    City create(City city);
    City update(City city);
    City obtainByIdWithDestinations(Long cityId);
}
