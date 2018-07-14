package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.JourneyGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Journey;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static br.com.cedran.city.model.BusinessError.DESTINATION_EXISTENT;

@AllArgsConstructor
@Service
public class CreateJourney {

    private ValidateRoute validateRoute;

    private JourneyGateway journeyGateway;

    public Journey execute(List<City> route, Duration journeyTime) {

        validateRoute.execute(route);

        City origin = route.get(0);
        route.remove(0);
        City destination = route.get(route.size() - 1);
        route.remove(route.size() - 1);

        /*Optional.ofNullable(journeyGateway.obtainByOriginAndDestination(origin.getId(), destination.getId()))
                .ifPresent((journey) -> {throw new BusinessException(DESTINATION_EXISTENT);});*/

        Journey journey = new Journey();
        journey.setOrigin(origin);
        journey.setDestination(destination);
        journey.setConnections(route);
        journey.setJourneyTime(journeyTime);

        return journeyGateway.create(journey);
    }

}
