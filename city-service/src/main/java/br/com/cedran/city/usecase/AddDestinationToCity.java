package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.gateway.DestinationGateway;
import br.com.cedran.city.model.BusinessError;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static br.com.cedran.city.model.BusinessError.CITY_NOT_EXISTENT;
import static br.com.cedran.city.model.BusinessError.DESTINATION_EXISTENT;

@AllArgsConstructor
@Service
public class AddDestinationToCity {

    private DestinationGateway destinationGateway;

    private CityGateway cityGateway;

    public Destination execute(Long originCityId, Long destinationCityId, Duration journeyTime) {

        City originCity = Optional.ofNullable(cityGateway.obtainById(originCityId)).orElseThrow(() -> new BusinessException(CITY_NOT_EXISTENT));
        if (Optional.ofNullable(originCity.getDestinations()).orElse(Collections.emptyList()).stream().anyMatch(destinationCity -> destinationCity.getDestinationCity().getId().equals(destinationCityId))) {
            throw new BusinessException(DESTINATION_EXISTENT);
        }

        City destinationCity = Optional.ofNullable(cityGateway.obtainById(destinationCityId)).orElseThrow(() -> new BusinessException(CITY_NOT_EXISTENT));


        Destination destination = new Destination();
        destination.setOriginCity(originCity);
        destination.setDestinationCity(destinationCity);
        destination.setJourneyTime(journeyTime);

        return destinationGateway.create(destination);
    }

}
