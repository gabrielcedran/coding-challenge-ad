package br.com.cedran.route.gateway.web.mvc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShortestRouteResponseDTO {

    private List<String> cities;
    private Long numberOfStops;

}
