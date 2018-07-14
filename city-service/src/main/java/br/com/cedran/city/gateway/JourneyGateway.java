package br.com.cedran.city.gateway;

import br.com.cedran.city.model.Journey;

public interface JourneyGateway {
    Journey obtainByOriginAndDestination(Long originCity, Long destinationCity);

    Journey create(Journey journey);

    Journey update(Journey journey);

    Journey obtainById(Long journeyId);
}
