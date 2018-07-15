package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.model.Destination;
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

        List<Long> processedCity = new ArrayList<>();
        // key: city id
        // value: cities
        Map<Long, List<City>> routesFound = new HashMap<>();
        List<Pair<City, List<City>>> citiesToBeProcessed = new ArrayList<>();

        setupFirstDestinations(originCityId, processedCity, routesFound, citiesToBeProcessed);

        processShortestRoute(destinationCityId, processedCity, routesFound, citiesToBeProcessed);


        return routesFound.get(destinationCityId);
    }

    private void setupFirstDestinations(Long originCityId, List<Long> processedCity, Map<Long, List<City>> routesFound, List<Pair<City, List<City>>> citiesToBeProcessed) {
        City originCity = cityGateway.obtainWithDestinationsById(originCityId);

        processedCity.add(originCity.getId());
        routesFound.put(originCity.getId(), new ArrayList<>());

        originCity.getDestinations().forEach(destination -> {
            List<City> route = new ArrayList<>(Collections.singletonList(destination.getCity()));
            routesFound.put(destination.getCity().getId(), route);
            citiesToBeProcessed.add(Pair.of(destination.getCity(), route));
        });
    }


    private void processShortestRoute(Long destinationCityId, List<Long> processedCity, Map<Long, List<City>> routesFound, List<Pair<City, List<City>>> citiesToBeProcessed) {
        while (!citiesToBeProcessed.isEmpty()) {

            if (routesFound.containsKey(destinationCityId)) {
                break;
            }

            Pair<City, List<City>> nextProcessingCity = citiesToBeProcessed.remove(0);
            City processingCity = cityGateway.obtainWithDestinationsById(nextProcessingCity.getLeft().getId());

            processedCity.add(processingCity.getId());

            processingCity.getDestinations().stream().forEach(destination -> {
                List<City> route = new ArrayList<>(nextProcessingCity.getRight());
                route.add(destination.getCity());

                routesFound.putIfAbsent(destination.getCity().getId(), route);

                if (addCityToListToBeProcessed(processedCity, citiesToBeProcessed, destination)) {
                    citiesToBeProcessed.add(Pair.of(destination.getCity(), route));
                }
            });

        }
    }

    private boolean addCityToListToBeProcessed(List<Long> processedCity, List<Pair<City, List<City>>> citiesToBeProcessed, Destination destination) {
        return !processedCity.contains(destination.getCity().getId()) &&
                citiesToBeProcessed.stream().noneMatch(cityToBeProcessed -> cityToBeProcessed.getLeft().getId().equals(destination.getCity().getId()));
    }
}
