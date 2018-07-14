package br.com.cedran.city

import br.com.cedran.city.gateway.CityGateway
import br.com.cedran.city.model.City
import br.com.cedran.city.usecase.CreateCity
import br.com.cedran.city.usecase.UpdateCity
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification


class UpdateCitySpec extends Specification {

    UpdateCity updateCity
    CityGateway cityGateway = Mock()

    def setup() {
        updateCity = new UpdateCity(cityGateway)
    }

    def "Update city successfully"() {
        given: "an existent city with id 1 and name Zaragoza"
        City city = new City(1L, "Zaragoza", null)
        and: "that city already exists"
        cityGateway.obtainById(1L) >> city
        and: "the city name is updated to a non existent one"
        city.name = "Granada"

        when: "the scenario executes"
        city = updateCity.execute(city)

        then: "the city id is not changed"
        city.id == 1
        and: "the city name is updated"
        city.name == "Granada"
        and: "the update method of the city gateway is called"
        1 * cityGateway.update(_) >> {args -> args[0] }
    }

    def "Update city not changing the name successfully"() {
        given: "an existent city with id 1 and name Zaragoza"
        City city = new City(1L, "Zaragoza", null)
        and: "that city name exists associated to the city being updated"
        cityGateway.obtainById(1L) >> city
        and: "the city already exists"
        cityGateway.obtainById(1L) >> city

        when: "the scenario executes"
        city = updateCity.execute(city)

        then: "the city id is not changed"
        city.id == 1
        and: "the city name is not updated"
        city.name == "Zaragoza"
        and: "the update method of the city gateway is called"
        1 * cityGateway.update(_) >> {args -> args[0] }
    }

    def "Update city name to an existent one"() {
        given: "an existent city with id 1 and name Zaragoza"
        City city = new City(1L, "Zaragoza", null)
        and: "the city has the name updated"
        city.name = "Granada"
        and: "that city name is already associated to another city"
        cityGateway.obtainByName(_) >> new City(2, "Granada", null)

        when: "the scenario executes"
        city = updateCity.execute(city)

        then: "an exception with the error code BE00001 is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == "BE00001"
        and: "update method is not called"
        0 * cityGateway.update(_)
    }

    def "Try update city not existent yet"() {
        given: "a non existent city with id 1 and name Zaragoza"
        City city = new City(1L, "Zaragoza", null)

        when: "the scenario executes"
        city = updateCity.execute(city)

        then: "an exception with error code BE00002 is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == "BE00002"
        and: "update method is not called"
        0 * cityGateway.update(_)
    }
}