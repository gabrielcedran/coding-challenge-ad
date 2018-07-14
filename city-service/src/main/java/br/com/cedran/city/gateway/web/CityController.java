package br.com.cedran.city.gateway.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CityController {

    @RequestMapping
    public String index() {
        return "Hello";
    }
}
