package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.model.Destination;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static br.com.cedran.route.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CalculateShortestRoute {

    private CityGateway cityGateway;

    private ExecutorService shortestRouteCalculationExecutorService;

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

            // Always process all the
            List<CompletableFuture<Pair<City, List<City>>>> processingCitiesCF = citiesToBeProcessed.stream()
                    .map(cityToBeProcessed -> CompletableFuture.supplyAsync(() -> Pair.of(cityGateway.obtainWithDestinationsById(cityToBeProcessed.getLeft().getId()), cityToBeProcessed.getRight()),
                            shortestRouteCalculationExecutorService)).collect(Collectors.toList());
            citiesToBeProcessed.clear();

            CompletableFuture<Void> allCFDone = CompletableFuture.allOf(processingCitiesCF.toArray(new CompletableFuture[processingCitiesCF.size()]));
            List<Pair<City, List<City>>> processingCities = allCFDone.thenApply(v -> processingCitiesCF.stream().map(CompletableFuture::join).collect(toList())).join();

            processingCities.forEach(processingPair -> {
                City processingCity = processingPair.getLeft();
                processedCity.add(processingCity.getId());

                processingCity.getDestinations().stream().forEach(destination -> {
                    List<City> route = new ArrayList<>(processingPair.getRight());
                    route.add(destination.getCity());

                    routesFound.putIfAbsent(destination.getCity().getId(), route);

                    if (addCityToListToBeProcessed(processedCity, citiesToBeProcessed, processingCities, destination)) {
                        citiesToBeProcessed.add(Pair.of(destination.getCity(), route));
                    }
                });
            });

        }
    }

    private boolean addCityToListToBeProcessed(List<Long> processedCity, List<Pair<City, List<City>>> citiesToBeProcessed, List<Pair<City, List<City>>> processingCities, Destination destination) {
        // Consider eligible to be processed the ones that has not been processed yet, are not in the list to be processed and are not in the current processing pipeline
        return !processedCity.contains(destination.getCity().getId()) &&
                citiesToBeProcessed.stream().noneMatch(cityToBeProcessed -> cityToBeProcessed.getLeft().getId().equals(destination.getCity().getId())) &&
                processingCities.stream().noneMatch(cityToBeProcessed -> cityToBeProcessed.getLeft().getId().equals(destination.getCity().getId()));
    }
}
