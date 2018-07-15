package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import br.com.cedran.route.usecase.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static br.com.cedran.route.model.ErrorCode.DESTINATION_NOT_EXISTENT;
import static br.com.cedran.route.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME;
import static br.com.cedran.route.model.ErrorCode.ORIGIN_NOT_EXISTENT;

@Service
@AllArgsConstructor
public class ValidateOriginAndDestination {

    private CityGateway cityGateway;

    public Pair<City, City> execute(Long originCityId, Long destinationCityId) {
        if (originCityId.equals(destinationCityId)) {
            throw new BusinessException(ORIGIN_AND_DESTINATION_THE_SAME);
        }

        CompletableFuture<City> originCityCF = CompletableFuture.supplyAsync(() -> cityGateway.obtainWithDestinationsById(originCityId));
        CompletableFuture<City> destinationCityCF = CompletableFuture.supplyAsync(() -> cityGateway.obtainWithDestinationsById(destinationCityId));

        try {
            Pair<City, City> originAndDestination = Pair.of(originCityCF.get(), destinationCityCF.get());
            if (originAndDestination.getLeft() == null) {
                throw new BusinessException(ORIGIN_NOT_EXISTENT);
            }
            if (originAndDestination.getRight() == null) {
                throw new BusinessException(DESTINATION_NOT_EXISTENT);
            }
            return originAndDestination;
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof NotFoundException) {
                throw (NotFoundException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

}
