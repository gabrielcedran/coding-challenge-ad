package br.com.cedran.route.gateway.web.feign.assembler;

import br.com.cedran.route.gateway.web.feign.dto.DestinationDTO;
import br.com.cedran.route.model.Destination;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DestinationAssembler {

    public static List<Destination> fromDestinationDTOS(List<DestinationDTO> destinationDTOS) {
        return Optional.ofNullable(destinationDTOS).orElse(Collections.emptyList()).stream().map(DestinationAssembler::fromDestinationDTOS).collect(Collectors.toList());
    }

    public static Destination fromDestinationDTOS(DestinationDTO destinationDTO) {
        Destination destination = new Destination();
        destination.setId(destinationDTO.getId());
        destination.setCity(CityAssembler.fromCityDTO(destinationDTO.getCity()));
        destination.setJourneyTime(Duration.ofMinutes(destinationDTO.getJourneyTimeInMinutes()));
        return destination;
    }

}
