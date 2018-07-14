package br.com.cedran.city.gateway.web.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DestinationRequestDTO {

    @NotNull
    private Long destinationCityId;
    @NotNull
    private Long journeyTimeInMinutes;

}
