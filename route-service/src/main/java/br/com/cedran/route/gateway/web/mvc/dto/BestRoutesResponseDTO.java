package br.com.cedran.route.gateway.web.mvc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BestRoutesResponseDTO {

    private FastestRouteResponseDTO fastestRouteResponseDTO;

    private ShortestRouteResponseDTO shortestRouteResponseDTO;

}
