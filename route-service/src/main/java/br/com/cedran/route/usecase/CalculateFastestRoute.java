package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.model.Destination;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

import static br.com.cedran.route.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME;

@Service
@AllArgsConstructor
public class CalculateFastestRoute {

    private CityGateway cityGateway;

    public Pair<Duration, List<City>> execute(Long originCity, Long destinationCity) {

        if (originCity.equals(destinationCity)) {
            throw new BusinessException(ORIGIN_AND_DESTINATION_THE_SAME);
        }

        List<Long> processedCities = new ArrayList<>();

        // key: city id
        // value: left = time | right = route
        Map<Long, Pair<Duration, List<City>>> definedDistances = new HashMap<>();

        // left: city
        // right: time
        TreeSet<Pair<City, Duration>> citiesToBeProcessed = new TreeSet<>((city1, city2) -> city1.getRight().equals(city2.getRight()) ? city1.getLeft().getId().compareTo(city2.getLeft().getId()) : city1.getRight().compareTo(city2.getRight()));

        setupFirstDestinations(originCity, processedCities, definedDistances, citiesToBeProcessed);

        processFastestRoute(processedCities, definedDistances, citiesToBeProcessed, destinationCity);

        return definedDistances.get(destinationCity);

    }

    private void setupFirstDestinations(Long originCityId, List<Long> processedCities, Map<Long, Pair<Duration, List<City>>> definedDistances, TreeSet<Pair<City, Duration>> citiesToBeProcessed) {
        City originCity = cityGateway.obtainWithDestinationsById(originCityId);
        definedDistances.putIfAbsent(originCity.getId(), Pair.of(Duration.ZERO, Collections.emptyList()));
        processedCities.add(originCity.getId());

        Optional.ofNullable(originCity.getDestinations()).orElse(Collections.emptyList()).forEach(destination -> {
            Duration journeyTime = destination.getJourneyTime();
            City destinationCity = destination.getCity();
            List<City> route = new ArrayList<>();
            route.add(destinationCity);
            definedDistances.put(destinationCity.getId(), Pair.of(journeyTime, route));
            citiesToBeProcessed.add(Pair.of(destinationCity, journeyTime));
        });
    }

    private void processFastestRoute(List<Long> processedCities, Map<Long, Pair<Duration, List<City>>> definedDistances, TreeSet<Pair<City, Duration>> citiesToBeProcessed, Long destinationCity) {
        while (!citiesToBeProcessed.isEmpty()) {

            if (!thereIsStillAChanceForFasterRoute(definedDistances, citiesToBeProcessed, destinationCity)) {
                break;
            }

            Pair<City, Duration> processingCity = citiesToBeProcessed.pollFirst();
            City city = cityGateway.obtainWithDestinationsById(processingCity.getLeft().getId());

            processedCities.add(city.getId());

            Optional.ofNullable(city.getDestinations()).orElse(Collections.emptyList()).forEach(destination -> {

                Pair<Duration, List<City>> timeFromTheOriginCity = definedDistances.get(city.getId());
                Duration journeyTime = timeFromTheOriginCity.getLeft().plus(destination.getJourneyTime());
                List<City> route = new ArrayList<>(timeFromTheOriginCity.getRight());
                route.add(destination.getCity());

                if (definedDistances.get(destination.getCity().getId()) == null ||
                        journeyTime.getSeconds() < definedDistances.get(destination.getCity().getId()).getLeft().getSeconds()) {
                    definedDistances.put(destination.getCity().getId(), Pair.of(journeyTime, route));
                }

                if (addCityToListToBeProcessed(processedCities, citiesToBeProcessed, destination)) {
                    citiesToBeProcessed.add(Pair.of(destination.getCity(), journeyTime));
                }
            });

        }
    }

    private boolean addCityToListToBeProcessed(List<Long> processedCities, TreeSet<Pair<City, Duration>> citiesToBeProcessed, Destination destination) {
        return !processedCities.contains(destination.getCity().getId()) && citiesToBeProcessed.stream().noneMatch(cityToBeProcess -> cityToBeProcess.getLeft().getId().equals(destination.getCity().getId()));
    }

    private boolean thereIsStillAChanceForFasterRoute(Map<Long, Pair<Duration, List<City>>> definedDistances, TreeSet<Pair<City, Duration>> citiesToBeProcessed, Long destinationCity) {
        return !definedDistances.containsKey(destinationCity) || citiesToBeProcessed.stream()
                .filter(cityToBeProcessed -> cityToBeProcessed.getRight().getSeconds() < definedDistances.get(destinationCity).getLeft().getSeconds())
                .findAny().map(obj -> true).orElse(Boolean.FALSE);
    }

}
