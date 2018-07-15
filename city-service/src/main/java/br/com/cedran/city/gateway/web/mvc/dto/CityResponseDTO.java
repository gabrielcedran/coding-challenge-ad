package br.com.cedran.city.gateway.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CityResponseDTO {

    private Long id;
    private String name;
    private List<DestinationResponseDTO> destinations;

}
