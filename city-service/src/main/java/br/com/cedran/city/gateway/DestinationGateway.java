package br.com.cedran.city.gateway;

import br.com.cedran.city.model.Destination;

public interface DestinationGateway {
    Destination obtainByOriginAndDestination(Long originCity, Long destinationCity);
    Destination create(Destination journey);
    void delete(Destination destination);
    Destination obtainById(Long id);
}
