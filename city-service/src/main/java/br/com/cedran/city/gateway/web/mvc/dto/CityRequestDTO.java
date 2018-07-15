package br.com.cedran.city.gateway.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CityRequestDTO {

    @NotNull
    @NotEmpty
    private String name;

}
