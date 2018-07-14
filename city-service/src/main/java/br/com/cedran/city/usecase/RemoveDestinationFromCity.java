package br.com.cedran.city.usecase;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.gateway.DestinationGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static br.com.cedran.city.model.BusinessError.CITY_NOT_EXISTENT;
import static br.com.cedran.city.model.BusinessError.DESTINATION_EXISTENT;
import static br.com.cedran.city.model.BusinessError.DESTINATION_NOt_EXISTENT;

@AllArgsConstructor
@Service
public class RemoveDestinationFromCity {

    private DestinationGateway destinationGateway;

    public void execute(Long originCityId, Long destinationCityId) {

        Destination destination = Optional.ofNullable(destinationGateway.obtainByOriginAndDestination(originCityId, destinationCityId))
                .orElseThrow(() -> new BusinessException(DESTINATION_NOt_EXISTENT));

        destinationGateway.delete(destination);
    }

}
