package br.com.cedran.city

import br.com.cedran.city.gateway.CityGateway
import br.com.cedran.city.model.City
import br.com.cedran.city.usecase.ValidateRoute
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification

class ValidateRouteSpec extends Specification {

    ValidateRoute validateRoute
    CityGateway cityGateway = Mock()

    def setup() {
        validateRoute = new ValidateRoute(cityGateway)
    }

    def "Validate list without duplications and all cities existent"() {

        given: "a list of cities representing a route with three different cities"
        def routes = [new City(1, "Zaragoza"),
                      new City(2, "Granada"),
                      new City(3, "Madrid")]
        and: "all of them are existent"
        cityGateway.obtainByIds(_) >> [new City(2, "Granada"),
                                       new City(1, "Zaragoza"),
                                       new City(3, "Madrid")]

        when: "that list is validated"
        validateRoute.execute(routes)

        then: "no exception is raised"
        noExceptionThrown()
    }

    def "Validate list with one element duplicated"() {

        given: "a list of cities representing a route with three different cities"
        def routes = [new City(1, "Zaragoza"),
                      new City(2, "Granada"),
                      new City(1, "Zaragoza")]

        when: "that list is validated"
        validateRoute.execute(routes)

        then: "an exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == "BE00003"
        and: "the obtain cities by ids is not called"
        0 * cityGateway.obtainByIds(_)
    }

    def "Validate list without duplications with invalid city"() {

        given: "a list of cities representing a route with three different cities"
        def routes = [new City(1, "Zaragoza"),
                      new City(2, "Granada"),
                      new City(3, "Madrid")]
        and: "one city is not existent"
        cityGateway.obtainByIds(_) >> [new City(1, "Zaragoza"),
                                       new City(3, "Madrid")]

        when: "that list is validated"
        validateRoute.execute(routes)

        then: "an exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == "BE00002"
    }
}
