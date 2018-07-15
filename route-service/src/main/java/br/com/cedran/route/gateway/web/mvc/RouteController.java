package br.com.cedran.route.gateway.web.mvc;

import br.com.cedran.route.gateway.web.mvc.assembler.CityAssembler;
import br.com.cedran.route.gateway.web.mvc.dto.BestRoutesResponseDTO;
import br.com.cedran.route.gateway.web.mvc.dto.FastestRouteResponseDTO;
import br.com.cedran.route.model.City;
import br.com.cedran.route.usecase.CalculateFastestRoute;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping(value = "/v1/route")
@AllArgsConstructor
public class RouteController {

    private CalculateFastestRoute calculateFastestRoute;

    @GetMapping(value = "/{originCityId}/{destinationCityId}")
    public BestRoutesResponseDTO obtainBestRoutes(@PathVariable(value = "originCityId") @NotNull Long originCityId, @PathVariable(value = "destinationCityId") @NotNull Long destinationCityId) {
        Pair<Duration, List<City>> fastestRouteFound = calculateFastestRoute.execute(originCityId, destinationCityId);

        return BestRoutesResponseDTO
                .builder()
                    .fastest(ofNullable(fastestRouteFound).map(fastestRoute ->
                        FastestRouteResponseDTO
                        .builder()
                                .journeyTimeInMinutes(fastestRoute.getLeft().toMinutes())
                                .cities(fastestRoute.getRight().stream().map(CityAssembler::fromCityDTO).collect(Collectors.toList()))
                        .build()).orElse(null))
                .build();
    }

}
