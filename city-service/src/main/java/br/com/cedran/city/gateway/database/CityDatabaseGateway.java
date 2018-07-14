package br.com.cedran.city.gateway.database;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityDatabaseGateway implements CityGateway {

    @Override
    public City obtainByName(String name) {
        return null;
    }

    @Override
    public City obtainById(Long id) {
        return null;
    }

    @Override
    public City create(City city) {
        return null;
    }

    @Override
    public City update(City city) {
        return null;
    }

    @Override
    public List<City> obtainByIds(List<Long> citiesIds) {
        return null;
    }
}
