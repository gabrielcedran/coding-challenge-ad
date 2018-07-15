package br.com.cedran.route.gateway.web.feign.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CityDTO {

    private Long id;
    private String name;
    private List<DestinationDTO> destinations;

}
