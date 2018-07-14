package br.com.cedran.city.gateway.web;

import br.com.cedran.city.gateway.web.assembler.DestinationAssembler;
import br.com.cedran.city.gateway.web.dtos.DestinationRequestDTO;
import br.com.cedran.city.gateway.web.dtos.DestinationResponseDTO;
import br.com.cedran.city.usecase.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping(value = "/v1/city/{originCityId}/destination")
@AllArgsConstructor
public class DestinationController {

    private AddDestinationToCity addDestinationToCity;
    private RemoveDestinationFromCity removeDestinationFromCity;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public DestinationResponseDTO createDestination(@PathVariable(value = "originCityId") Long originCityId, @RequestBody @Valid DestinationRequestDTO destinationResponseDTO) {
        return DestinationAssembler.fromDestination(
                addDestinationToCity.execute(originCityId, destinationResponseDTO.getDestinationCityId(), Duration.ofMinutes(destinationResponseDTO.getJourneyTimeInMinutes())));
    }

    @DeleteMapping(value = "/{destinationCityId}")
    public void removeDestination(@PathVariable(value = "originCityId") Long originCityId, @PathVariable(value = "destinationCityId") Long destinationCityId) {
        removeDestinationFromCity.execute(originCityId, destinationCityId);
    }

}
