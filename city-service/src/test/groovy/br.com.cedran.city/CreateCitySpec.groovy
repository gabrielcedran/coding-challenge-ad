package br.com.cedran.city

import br.com.cedran.city.gateway.CityGateway
import br.com.cedran.city.model.City
import br.com.cedran.city.usecase.CreateCity
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification


class CreateCitySpec extends Specification {

    CreateCity createCity
    CityGateway cityGateway = Mock()

    def setup() {
        createCity = new CreateCity(cityGateway)
    }

    def "Create city successfully"() {
        given: "a city name that does not exist yet"
        String cityName = "Zaragoza"

        when: "the scenario executes"
        City city = createCity.execute(cityName)

        then: "the city created is returned containing id 1"
        city.id == 1
        city.name == "Zaragoza"
        and: "the create method of the city gateway is called"
        1 * cityGateway.create(_) >> {args ->
            args[0].id = 1L
            args[0]
        }
    }

    def "Create city with a name already existent"() {
        given: "a city name that exists"
        String cityName = "Zaragoza"
        cityGateway.obtainByName(cityName) >> new City()

        when: "the scenario executes"
        City city = createCity.execute(cityName)

        then: "a business exception with the error BE00001 is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == "BE00001"

        and: "the create method of the city gateway is not called"
        0 * cityGateway.create(_)
    }

}