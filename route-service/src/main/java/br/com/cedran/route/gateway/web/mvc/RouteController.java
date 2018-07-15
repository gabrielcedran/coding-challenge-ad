package br.com.cedran.route.gateway.web.mvc;

import br.com.cedran.route.gateway.web.mvc.assembler.CityAssembler;
import br.com.cedran.route.gateway.web.mvc.dto.BestRoutesResponseDTO;
import br.com.cedran.route.gateway.web.mvc.dto.FastestRouteResponseDTO;
import br.com.cedran.route.gateway.web.mvc.dto.ShortestRouteResponseDTO;
import br.com.cedran.route.model.City;
import br.com.cedran.route.usecase.CalculateFastestRoute;
import br.com.cedran.route.usecase.CalculateShortestRoute;
import br.com.cedran.route.usecase.ValidateOriginAndDestination;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping(value = "/v1/route")
@AllArgsConstructor
public class RouteController {


    private ValidateOriginAndDestination validateOriginAndDestination;
    private CalculateFastestRoute calculateFastestRoute;
    private CalculateShortestRoute calculateShortestRoute;

    @GetMapping(value = "/{originCityId}/{destinationCityId}")
    public BestRoutesResponseDTO obtainBestRoutes(@PathVariable(value = "originCityId") @NotNull Long originCityId, @PathVariable(value = "destinationCityId") @NotNull Long destinationCityId) {

        Pair<City, City> originAndDestination = validateOriginAndDestination.execute(originCityId, destinationCityId);

        CompletableFuture<Pair<Duration, List<City>>> fastestRouteCF = CompletableFuture.supplyAsync(() -> calculateFastestRoute.execute(originCityId, destinationCityId));
        CompletableFuture<List<City>> shortestRouteCF = CompletableFuture.supplyAsync(() -> calculateShortestRoute.execute(originCityId, destinationCityId));

        Pair<Duration, List<City>> fastestRouteFound;
        List<City> shortestRouteFound;
        try {
            fastestRouteFound = fastestRouteCF.get();
            shortestRouteFound = shortestRouteCF.get();
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof BusinessException) {
                throw (BusinessException) e.getCause();
            }
            throw new RuntimeException(e);
        }

        return BestRoutesResponseDTO
                .builder()
                    .fastest(ofNullable(fastestRouteFound).map(fastestRoute ->
                        FastestRouteResponseDTO
                        .builder()
                                .journeyTimeInMinutes(fastestRoute.getLeft().toMinutes())
                                .cities(fastestRoute.getRight().stream().map(CityAssembler::fromCityDTO).collect(Collectors.toList()))
                        .build()).orElse(null))
                    .shortest(ofNullable(shortestRouteFound).map(shortestRoute ->
                        ShortestRouteResponseDTO
                        .builder()
                                .numberOfStops(shortestRoute.size())
                                .cities(CityAssembler.fromCityDTOS(shortestRoute))
                        .build()).orElse(null))
                .build();
    }

}
