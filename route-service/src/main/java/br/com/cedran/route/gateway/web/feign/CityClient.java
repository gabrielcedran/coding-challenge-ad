package br.com.cedran.route.gateway.web.feign;

import br.com.cedran.route.gateway.web.feign.dto.CityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("cityApi")
@RequestMapping(value = "/v1/city")
public interface CityClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{cityId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    CityDTO byIdWithDestinations(@PathVariable("cityId") Long cityId);

}
