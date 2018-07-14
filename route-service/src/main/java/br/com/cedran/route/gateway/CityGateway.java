package br.com.cedran.route.gateway;

import br.com.cedran.route.model.City;

public interface CityGateway {
    City obtainById(Long cityOrigin);
}
