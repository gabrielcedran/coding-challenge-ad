package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.gateway.DestinationGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static br.com.cedran.city.model.ErrorCode.*;

@AllArgsConstructor
@Service
public class AddDestinationToCity {

    private DestinationGateway destinationGateway;

    private CityGateway cityGateway;

    public Destination execute(Long originCityId, Long destinationCityId, Duration journeyTime) {

        if (originCityId.equals(destinationCityId)) {
            throw new BusinessException(ORIGIN_AND_DESTINATION_THE_SAME);
        }

        Optional.ofNullable(journeyTime).filter(time -> time.toMinutes() > 0).orElseThrow(() -> new BusinessException(JOURNEY_TIME_INVALID));

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
