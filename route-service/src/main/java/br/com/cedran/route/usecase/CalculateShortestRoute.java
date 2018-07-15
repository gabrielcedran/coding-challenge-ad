package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.com.cedran.route.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME;

@Service
@AllArgsConstructor
public class CalculateShortestRoute {

    private CityGateway cityGateway;

    public List<City> execute(Long originCityId, Long destinationCityId) {

        if (originCityId.equals(destinationCityId)) {
            throw new BusinessException(ORIGIN_AND_DESTINATION_THE_SAME);
        }

        // key: city id
        // value: cities
        Map<Long, List<City>> routesFound = new HashMap<>();
        List<Pair<City, List<City>>> citiesToBeProcessed = new ArrayList<>();

        City originCity = cityGateway.obtainWithDestinationsById(originCityId);
        routesFound.put(originCity.getId(), new ArrayList<>());
        originCity.getDestinations().forEach(destination -> citiesToBeProcessed.add(Pair.of(destination.getCity(), new ArrayList<>(Collections.singletonList(destination.getCity())))));

        while (!citiesToBeProcessed.isEmpty()) {

            if (routesFound.containsKey(destinationCityId)) {
                break;
            }

            Pair<City, List<City>> nextProcessingCity = citiesToBeProcessed.remove(0);
            City processingCity = cityGateway.obtainWithDestinationsById(nextProcessingCity.getLeft().getId());

            routesFound.put(processingCity.getId(), nextProcessingCity.getRight());

            processingCity.getDestinations().stream().forEach(destination -> {
                if (!routesFound.containsKey(destination.getCity().getId()) &&
                        citiesToBeProcessed.stream().noneMatch(cityToBeProcessed -> cityToBeProcessed.getLeft().getId().equals(destination.getCity().getId()))) {
                    List<City> route = new ArrayList<>(nextProcessingCity.getRight());
                    route.add(destination.getCity());
                    citiesToBeProcessed.add(Pair.of(destination.getCity(), route));
                }
            });

        }


        return routesFound.get(destinationCityId);
    }
}
