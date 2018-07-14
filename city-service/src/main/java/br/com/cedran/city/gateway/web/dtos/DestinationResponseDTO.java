package br.com.cedran.city.gateway.web.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationResponseDTO {
    private Long id;
    private CityResponseDTO originCity;
    private CityResponseDTO destinationCity;
    private Long journeyTimeInMinutes;
}
