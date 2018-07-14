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
import static br.com.cedran.city.model.BusinessError.DESTINATION_NOt_EXISTENT;

@AllArgsConstructor
@Service
public class UpdateJourney {

    private ValidateRoute validateRoute;

    private JourneyGateway journeyGateway;

    public Journey execute(Long journeyId, List<City> route, Duration journeyTime) {

        Journey journey = Optional.ofNullable(journeyGateway.obtainById(journeyId)).orElseThrow(() -> new BusinessException(DESTINATION_NOt_EXISTENT));

        validateRoute.execute(route);

        City origin = route.get(0);
        route.remove(0);
        City destination = route.get(route.size() - 1);
        route.remove(route.size() - 1);

        /*Optional.ofNullable(journeyGateway.obtainByOriginAndDestination(origin.getId(), destination.getId()))
                .filter(foundJourney -> !foundJourney.getId().equals(journeyId))
                .ifPresent((foundJourney) -> {throw new BusinessException(DESTINATION_EXISTENT);});*/

        journey.setOrigin(origin);
        journey.setDestination(destination);
        journey.setConnections(route);
        journey.setJourneyTime(journeyTime);

        return journeyGateway.update(journey);
    }

}
