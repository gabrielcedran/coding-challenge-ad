package br.com.cedran.city.gateway.assembler;

import br.com.cedran.city.gateway.database.mysql.entity.CityEntity;
import br.com.cedran.city.model.City;
import org.hibernate.Hibernate;

public class CityAssembler {

    public static City fromCityEntity(CityEntity cityEntity, boolean destinations) {
        City city = new City();
        city.setId(cityEntity.getId());
        city.setName(cityEntity.getName());
        if (destinations) {
            city.setDestinations(DestinationAssembler.fromDestinationEntities(cityEntity.getDestinations()));
        }
        return city;
    }

    public static CityEntity fromCity(City city) {
        CityEntity cityEntity = new CityEntity();
        cityEntity.setId(city.getId());
        cityEntity.setName(city.getName());
        cityEntity.setDestinations(DestinationAssembler.fromDestinations(city.getDestinations()));
        return cityEntity;
    }

}
