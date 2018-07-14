package br.com.cedran.city.gateway.assembler;

import br.com.cedran.city.gateway.database.mysql.entity.DestinationEntity;
import br.com.cedran.city.model.Destination;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DestinationAssembler {

    public static Destination fromDestinationEntity(DestinationEntity destinationEntity) {
        Destination destination = new Destination();
        destination.setId(destinationEntity.getId());
        destination.setOriginCity(CityAssembler.fromCityEntity(destinationEntity.getOrigin(), false));
        destination.setDestinationCity(CityAssembler.fromCityEntity(destinationEntity.getDestination(), false));
        destination.setJourneyTime(Duration.ofMinutes(destinationEntity.getJourneyTimeInMinutes()));
        return destination;
    }

    public static DestinationEntity fromDestination(Destination destination) {
        DestinationEntity destinationEntity = new DestinationEntity();
        destinationEntity.setId(destination.getId());
        destinationEntity.setOrigin(CityAssembler.fromCity(destination.getOriginCity()));
        destinationEntity.setDestination(CityAssembler.fromCity(destination.getDestinationCity()));
        destinationEntity.setJourneyTimeInMinutes(destination.getJourneyTime().toMinutes());
        return destinationEntity;
    }

    public static List<Destination> fromDestinationEntities(List<DestinationEntity> destinations) {
        return Optional.ofNullable(destinations).orElse(Collections.emptyList()).stream().map(DestinationAssembler::fromDestinationEntity).collect(Collectors.toList());
    }

    public static List<DestinationEntity> fromDestinations(List<Destination> destinations) {
        return Optional.ofNullable(destinations).orElse(Collections.emptyList()).stream().map(DestinationAssembler::fromDestination).collect(Collectors.toList());
    }
}
