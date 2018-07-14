package br.com.cedran.city.gateway.web.assembler;

import br.com.cedran.city.gateway.web.dtos.DestinationResponseDTO;
import br.com.cedran.city.model.Destination;

import java.util.List;
import java.util.stream.Collectors;

public class DestinationAssembler {
    public static List<DestinationResponseDTO> fromDestination(List<Destination> destinations) {
        return destinations.stream().map(DestinationAssembler::fromDestination).collect(Collectors.toList());
    }

    public static DestinationResponseDTO fromDestination(Destination destination) {
        DestinationResponseDTO destinationResponseDTO = new DestinationResponseDTO();
        destinationResponseDTO.setId(destination.getId());
        destinationResponseDTO.setOriginCity(CityAssembler.fromCity(destination.getOriginCity(), false));
        destinationResponseDTO.setDestinationCity(CityAssembler.fromCity(destination.getDestinationCity(), false));
        destinationResponseDTO.setJourneyTimeInMinutes(destination.getJourneyTime().toMinutes());
        return destinationResponseDTO;
    }
}
