package br.com.cedran.city.gateway.database;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.gateway.assembler.CityAssembler;
import br.com.cedran.city.gateway.database.mysql.CityDatabaseRepository;
import br.com.cedran.city.gateway.database.mysql.entity.CityEntity;
import br.com.cedran.city.model.City;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CityDatabaseGateway implements CityGateway {

    private CityDatabaseRepository cityDatabaseRepository;

    @Override
    public City obtainByName(String name) {
        return  cityDatabaseRepository.findByNameContainingIgnoreCase(name).map(cityEntity -> CityAssembler.fromCityEntity(cityEntity, false)).orElse(null);
    }

    @Override
    public City obtainById(Long id) {
        return cityDatabaseRepository.findById(id).map(cityEntity -> CityAssembler.fromCityEntity(cityEntity, false)).orElse(null);
    }

    @Override
    public City create(City city) {
        CityEntity cityEntity = cityDatabaseRepository.save(CityAssembler.fromCity(city));
        return CityAssembler.fromCityEntity(cityEntity, false);
    }

    @Override
    public City update(City city) {
        CityEntity cityEntity = cityDatabaseRepository.save(CityAssembler.fromCity(city));
        return CityAssembler.fromCityEntity(cityEntity, false);
    }

    @Override
    public City obtainByIdWithDestinations(Long cityId) {
        return cityDatabaseRepository.obtainByIdWithDestinations(cityId).map(cityEntity -> CityAssembler.fromCityEntity(cityEntity, true)).orElse(null);
    }

}
