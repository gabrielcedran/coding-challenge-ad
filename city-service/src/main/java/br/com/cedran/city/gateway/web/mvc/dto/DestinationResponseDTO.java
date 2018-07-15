package br.com.cedran.city.gateway.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationResponseDTO {
    private Long id;
    private CityResponseDTO city;
    private Long journeyTimeInMinutes;
}
