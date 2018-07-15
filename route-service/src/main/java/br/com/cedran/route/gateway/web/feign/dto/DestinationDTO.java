package br.com.cedran.route.gateway.web.feign.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationDTO {

    private Long id;
    private CityDTO city;
    private Long journeyTimeInMinutes;

}
