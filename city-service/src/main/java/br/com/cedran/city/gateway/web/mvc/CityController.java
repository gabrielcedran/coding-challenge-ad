package br.com.cedran.city.gateway.web.mvc;

import br.com.cedran.city.gateway.web.assembler.CityAssembler;
import br.com.cedran.city.gateway.web.mvc.dto.CityRequestDTO;
import br.com.cedran.city.gateway.web.mvc.dto.CityResponseDTO;
import br.com.cedran.city.usecase.CreateCity;
import br.com.cedran.city.usecase.ObtainCityWithDestinations;
import br.com.cedran.city.usecase.UpdateCity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/v1/city")
@AllArgsConstructor
public class CityController {

    private CreateCity createCity;
    private UpdateCity updateCity;
    private ObtainCityWithDestinations obtainCityWithDestinations;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CityResponseDTO createCity(@RequestBody @Valid CityRequestDTO city) {
        return CityAssembler.fromCity(createCity.execute(city.getName()), false);
    }

    @PutMapping(value = "/{cityId}")
    public CityResponseDTO updateCity(@PathVariable(value = "cityId") Long cityId, @RequestBody @Valid CityRequestDTO city) {
        return CityAssembler.fromCity(updateCity.execute(CityAssembler.fromCityRequestDTO(cityId, city)), false);
    }

    @GetMapping(value = "/{cityId}")
    public CityResponseDTO obtainCityWithDestinations(@PathVariable(value = "cityId") @NotNull Long cityId) {
        return CityAssembler.fromCity(obtainCityWithDestinations.execute(cityId), true);
    }

}
