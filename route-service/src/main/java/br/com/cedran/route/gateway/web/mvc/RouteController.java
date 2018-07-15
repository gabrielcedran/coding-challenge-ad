package br.com.cedran.route.gateway.web.mvc;

import br.com.cedran.route.gateway.web.mvc.dto.BestRoutesResponseDTO;
import br.com.cedran.route.gateway.web.mvc.dto.FastestRouteResponseDTO;
import br.com.cedran.route.usecase.CalculateFastestRoute;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/v1/route")
@AllArgsConstructor
public class RouteController {

    private CalculateFastestRoute calculateFastestRoute;

    @GetMapping(value = "/{cityIdFrom}/{cityIdTo}")
    public BestRoutesResponseDTO obtainBestRoutes(@PathVariable(value = "cityIdFrom") @NotNull Long cityIdFrom, @PathVariable(value = "cityIdTo") @NotNull Long cityIdTo) {
        calculateFastestRoute.execute(cityIdFrom, cityIdTo);

        return BestRoutesResponseDTO.builder().fastestRouteResponseDTO(FastestRouteResponseDTO.builder().build()).build();
    }

}
