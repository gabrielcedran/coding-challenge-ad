package br.com.cedran.city.gateway;

import br.com.cedran.city.model.City;

import java.util.List;

public interface CityGateway {

    City obtainByName(String name);
    City obtainById(Long id);
    City create(City city);
    City update(City city);

    List<City> obtainByIds(List<Long> citiesIds);
}
