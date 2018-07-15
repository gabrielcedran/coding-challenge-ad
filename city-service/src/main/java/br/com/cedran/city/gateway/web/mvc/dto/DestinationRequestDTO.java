package br.com.cedran.city.gateway.web.mvc.dto;

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
