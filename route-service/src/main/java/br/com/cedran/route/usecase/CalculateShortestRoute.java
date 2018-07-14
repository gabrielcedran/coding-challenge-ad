package br.com.cedran.route.usecase;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.model.City;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CalculateShortestRoute {

    private CityGateway cityGateway;

    public Pair<Long, List<Long>> execute(Long cityOrigin, Long cityDestination) {

        // 1st key = processed city
        // 2nd key = destination city
        // value = pair<time, list<route>>
        Map<Long, Map<Long, Pair<Long, List<Long>>>> processedCities = new HashMap<>();
        // key = destination city
        // value = pair<time, list<route>>
        Map<Long, Pair<Long, List<Long>>> calculatedTime = new HashMap<>();

        // key = time
        // value = pair<city, list<route>
        TreeMap<Long, Pair<Long, List<Long>>> toBeProcessed = new TreeMap<>();

        City city = cityGateway.obtainById(cityOrigin);
        processedCities.putIfAbsent(city.getId(), calculatedTime);

        city.getDestinations().forEach(destination -> {
            Long journeyTime = destination.getJourneyTime().getSeconds();
            Long destinationCityId = destination.getCityId();
            List<Long> route = new ArrayList<>();
            route.add(destination.getCityId());
            calculatedTime.put(destinationCityId, Pair.of(journeyTime, route));
            toBeProcessed.put(journeyTime, Pair.of(destinationCityId, route));
        });


        while (!toBeProcessed.isEmpty()) {
            Pair<Long, List<Long>> processingCity = toBeProcessed.pollFirstEntry().getValue();
            City city2 = cityGateway.obtainById(processingCity.getLeft());

            processedCities.put(city2.getId(), toBeProcessed);

            city2.getDestinations().forEach(destination -> {

                Pair<Long, List<Long>> timeFromTheOriginCity = calculatedTime.get(city2.getId());
                Long journeyTime = timeFromTheOriginCity.getLeft() + destination.getJourneyTime().getSeconds();
                List<Long> route = new ArrayList<>(timeFromTheOriginCity.getRight());
                route.add(destination.getCityId());

                if (calculatedTime.get(destination.getCityId()) == null ||
                        timeFromTheOriginCity.getLeft() + destination.getJourneyTime().getSeconds() < calculatedTime.get(destination.getCityId()).getLeft()) {
                    calculatedTime.put(destination.getCityId(), Pair.of(journeyTime, route));
                }

                if (!processedCities.containsKey(destination.getCityId())) {
                    toBeProcessed.put(journeyTime, Pair.of(destination.getCityId(), route));
                }
            });
        }

        return processedCities.get(cityOrigin).get(cityDestination);

    }

}
