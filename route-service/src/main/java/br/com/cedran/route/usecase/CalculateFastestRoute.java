package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import br.com.cedran.route.model.Destination;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CalculateFastestRoute {

    private CityGateway cityGateway;

    public Pair<Long, List<Long>> execute(Long originCity, Long destinationCity) {

        List<Long> processedCities = new ArrayList<>();

        // key = city id
        // left = time | right = route
        Map<Long, Pair<Long, List<Long>>> definedDistances = new HashMap<>();

        // left = cityId
        // middle = time
        // right = route
        TreeSet<Pair<Long, Long>> citiesToBeProcessed = new TreeSet<>((city1, city2) -> city1.getRight().equals(city2.getRight()) ? city1.getLeft().compareTo(city2.getLeft()) : city1.getRight().compareTo(city2.getRight()));

        setupFirstDestinations(originCity, processedCities, definedDistances, citiesToBeProcessed);

        processFastestRoute(processedCities, definedDistances, citiesToBeProcessed, destinationCity);

        return definedDistances.get(destinationCity);

    }

    private void setupFirstDestinations(Long originCityId, List<Long> processedCities, Map<Long, Pair<Long, List<Long>>> definedDistances, TreeSet<Pair<Long, Long>> citiesToBeProcessed) {
        City originCity = cityGateway.obtainWithDestinationsById(originCityId);
        definedDistances.putIfAbsent(originCity.getId(), Pair.of(0L, Collections.emptyList()));
        processedCities.add(originCity.getId());

        Optional.ofNullable(originCity.getDestinations()).orElse(Collections.emptyList()).forEach(destination -> {
            Long journeyTime = destination.getJourneyTime().getSeconds();
            Long destinationCityId = destination.getCityId();
            List<Long> route = new ArrayList<>();
            route.add(destination.getCityId());
            definedDistances.put(destinationCityId, Pair.of(journeyTime, route));
            citiesToBeProcessed.add(Pair.of(destinationCityId, journeyTime));
        });
    }

    private void processFastestRoute(List<Long> processedCities, Map<Long, Pair<Long, List<Long>>> definedDistances, TreeSet<Pair<Long, Long>> citiesToBeProcessed, Long destinationCity) {
        while (!citiesToBeProcessed.isEmpty()) {

            if (!thereIsStillAChanceForFasterRoute(definedDistances, citiesToBeProcessed, destinationCity)) {
                break;
            }

            Pair<Long, Long> processingCity = citiesToBeProcessed.pollFirst();
            City city = cityGateway.obtainWithDestinationsById(processingCity.getLeft());

            processedCities.add(city.getId());

            Optional.ofNullable(city.getDestinations()).orElse(Collections.emptyList()).forEach(destination -> {

                Pair<Long, List<Long>> timeFromTheOriginCity = definedDistances.get(city.getId());
                Long journeyTime = timeFromTheOriginCity.getLeft() + destination.getJourneyTime().getSeconds();
                List<Long> route = new ArrayList<>(timeFromTheOriginCity.getRight());
                route.add(destination.getCityId());

                if (definedDistances.get(destination.getCityId()) == null ||
                        journeyTime < definedDistances.get(destination.getCityId()).getLeft()) {
                    definedDistances.put(destination.getCityId(), Pair.of(journeyTime, route));
                }

                if (addCityToListToBeProcessed(processedCities, citiesToBeProcessed, destination)) {
                    citiesToBeProcessed.add(Pair.of(destination.getCityId(), journeyTime));
                }
            });

        }
    }

    private boolean addCityToListToBeProcessed(List<Long> processedCities, TreeSet<Pair<Long, Long>> citiesToBeProcessed, Destination destination) {
        return !processedCities.contains(destination.getCityId()) && citiesToBeProcessed.stream().noneMatch(cityToBeProcess -> cityToBeProcess.getLeft().equals(destination.getCityId()));
    }

    private boolean thereIsStillAChanceForFasterRoute(Map<Long, Pair<Long, List<Long>>> definedDistances, TreeSet<Pair<Long, Long>> citiesToBeProcessed, Long destinationCity) {
        return !definedDistances.containsKey(destinationCity) || citiesToBeProcessed.stream()
                .filter(cityToBeProcessed -> cityToBeProcessed.getRight() < definedDistances.get(destinationCity).getLeft())
                .findAny().map(obj -> true).orElse(Boolean.FALSE);
    }

}
