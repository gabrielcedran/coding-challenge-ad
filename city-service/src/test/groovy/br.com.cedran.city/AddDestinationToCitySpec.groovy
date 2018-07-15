package br.com.cedran.city

import br.com.cedran.city.gateway.CityGateway
import br.com.cedran.city.gateway.DestinationGateway
import br.com.cedran.city.model.City
import br.com.cedran.city.model.Destination
import br.com.cedran.city.model.ErrorCode
import br.com.cedran.city.usecase.AddDestinationToCity
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification

import java.time.Duration

import static br.com.cedran.city.model.ErrorCode.CITY_NOT_EXISTENT
import static br.com.cedran.city.model.ErrorCode.DESTINATION_EXISTENT
import static br.com.cedran.city.model.ErrorCode.JOURNEY_TIME_INVALID
import static br.com.cedran.city.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME

class AddDestinationToCitySpec extends Specification {

    AddDestinationToCity addDestinationToCity

    DestinationGateway destinationGateway = Mock()

    CityGateway cityGateway = Mock()

    def setup() {
        addDestinationToCity = new AddDestinationToCity(destinationGateway, cityGateway)
    }


    def "create destination successfully"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "both exist"
        cityGateway.obtainById(originCityId) >> new City(originCityId, "Granada", null)
        cityGateway.obtainById(destinationCityId) >> new City(destinationCityId, "Madrid", null)

        when: "the add destination to city uc is executed"
        Destination destination = addDestinationToCity.execute(originCityId, destinationCityId, Duration.ofHours(1))

        then: "destination created has id"
        destination.id == 1
        and: "origin as Granada"
        destination.originCity.id == 1
        and: "destination as Madrid"
        destination.destinationCity.id == 2
        and: "create method of the destination gateway is called"
        1 * destinationGateway.create(_) >> {args ->
            args[0].id = 1
            args[0]
        }
    }

    def "invalid origin city"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "only the destination city exists"
        cityGateway.obtainById(destinationCityId) >> new City(destinationCityId, "Madrid", null)

        when: "the add destination to city uc is executed"
        Destination destination = addDestinationToCity.execute(originCityId, destinationCityId, Duration.ofHours(1))

        then: "no destination is created"
        destination == null
        and: "a business exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == CITY_NOT_EXISTENT.errorCode
        and: "create method of the destination gateway is not called"
        0 * destinationGateway.create(_)
    }

    def "invalid destination city"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "only the origin city exists"
        cityGateway.obtainById(originCityId) >> new City(originCityId, "Granada", null)

        when: "the add destination to city uc is executed"
        Destination destination = addDestinationToCity.execute(originCityId, destinationCityId, Duration.ofHours(1))

        then: "no destination is created"
        destination == null
        and: "a business exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == CITY_NOT_EXISTENT.errorCode
        and: "create method of the destination gateway is not called"
        0 * destinationGateway.create(_)
    }

    def "destination already existent"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "both exist but the destination is already associated to the origin"
        City originCity = new City(originCityId, "Granada", null)
        City destinationCity = new City(destinationCityId, "Madrid", null)
        Destination destination = new Destination(1, originCity, destinationCity, Duration.ofHours(1))
        originCity.destinations = [destination]
        cityGateway.obtainById(originCityId) >> originCity
        cityGateway.obtainById(destinationCityId) >> destinationCity

        when: "the add destination to city uc is executed"
        addDestinationToCity.execute(originCityId, destinationCityId, Duration.ofHours(1))

        then: "a business exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == DESTINATION_EXISTENT.errorCode
        and: "create method of the destination gateway is not called"
        0 * destinationGateway.create(_)
    }

    def "Test origin and destination the same" () {
        given: "the origin id 1"
        Long originId = 1
        and: "the destination id 1"
        Long destinationId = 1

        when: "the fastest between city 5 and city 12 is calculated"
        addDestinationToCity.execute(originId, destinationId, Duration.ofMinutes(100))

        then: "a business exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == ORIGIN_AND_DESTINATION_THE_SAME.errorCode
        be.message == ORIGIN_AND_DESTINATION_THE_SAME.message

    }

    def "Test add destination with journey time as zero" () {
        given: "the origin id 1"
        Long originId = 1
        and: "the destination id 2"
        Long destinationId = 2

        when: "the fastest between city 5 and city 12 is calculated"
        addDestinationToCity.execute(originId, destinationId, Duration.ofMinutes(0))

        then: "a business exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == JOURNEY_TIME_INVALID.errorCode
        be.message == JOURNEY_TIME_INVALID.message

    }
}
