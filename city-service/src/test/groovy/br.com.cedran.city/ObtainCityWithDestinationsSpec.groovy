package br.com.cedran.city

import br.com.cedran.city.gateway.CityGateway
import br.com.cedran.city.model.City
import br.com.cedran.city.model.Destination
import br.com.cedran.city.usecase.ObtainCityWithDestinations
import spock.lang.Specification

import java.time.Duration

class ObtainCityWithDestinationsSpec extends Specification {

    ObtainCityWithDestinations obtainCityWithDestinations

    CityGateway cityGateway = Mock()

    def setup() {
        obtainCityWithDestinations = new ObtainCityWithDestinations(cityGateway)
    }

    def "obtain city with its destinations by id"() {
        given: "a city with two destinations"
        City zaragoza = new City(1L, "Zaragoza", null)
        City granada = new City(2L, "Granada", null)
        City madrid = new City(3L, "Madrid", null)
        zaragoza.destinations = [new Destination(10, zaragoza, granada, Duration.ofHours(1)), new Destination(20, zaragoza, madrid, Duration.ofHours(2))]
        cityGateway.obtainByIdWithDestinations(1L) >> zaragoza

        when: "it is retrieved"
        City returnedCity = obtainCityWithDestinations.execute(1L)

        then: "Zaragoza is returned"
        returnedCity.id == 1L
        returnedCity.name == "Zaragoza"
        and: "it has two destinations"
        returnedCity.destinations.size() == 2
        and: "the first city is Granada"
        returnedCity.destinations[0].id == 10L
        returnedCity.destinations[0].destinationCity.id == 2L
        returnedCity.destinations[0].destinationCity.name == "Granada"
        and: "the second city is Madrid"
        returnedCity.destinations[1].id == 20L
        returnedCity.destinations[1].destinationCity.id == 3L
        returnedCity.destinations[1].destinationCity.name == "Madrid"

    }
}
