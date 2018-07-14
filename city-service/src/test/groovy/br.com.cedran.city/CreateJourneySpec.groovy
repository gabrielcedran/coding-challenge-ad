package br.com.cedran.city

import br.com.cedran.city.gateway.JourneyGateway
import br.com.cedran.city.model.BusinessError
import br.com.cedran.city.model.City
import br.com.cedran.city.model.Journey
import br.com.cedran.city.usecase.CreateJourney
import br.com.cedran.city.usecase.ValidateRoute
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification

import java.time.Duration

import static br.com.cedran.city.model.BusinessError.DESTINATION_EXISTENT
import static br.com.cedran.city.model.BusinessError.ROUTE_WITH_DUPLICATED_CITY

class CreateJourneySpec extends Specification {

    CreateJourney createJourney

    ValidateRoute validateRoute = Mock()

    JourneyGateway journeyGateway = Mock()

    def setup() {
        createJourney = new CreateJourney(validateRoute, journeyGateway)
    }


    def "Create journey successfully"() {
        given: "a list representing an ordered route from Zaragoza to Granada"
        def route = [new City(1, "Zaragoza"), new City(10, "Madrid"), new City(3, "Malaga"), new City(7, "Granada")]
        and: "and a journey time of 26h 30m"
        Duration journeyTime = Duration.ofMinutes(1590)

        when: "the journey is creation is executed"
        Journey journey = createJourney.execute(route, journeyTime)

        then: "the returned journey has id set"
        journey.id == 1
        and: "the origin city is Zaragoza"
        journey.origin.id == 1
        journey.origin.name == "Zaragoza"
        and: "the destination Granada"
        journey.destination.id == 7
        journey.destination.name == "Granada"
        and: "a connection passing first in Madrid"
        journey.connections[0].id == 10
        journey.connections[0].name == "Madrid"
        and: "and then passing in Malaga"
        journey.connections[1].id == 3
        journey.connections[1].name == "Malaga"
        and: "the journey time is "
        journey.journeyTime.getSeconds()/60 == 1590
        and: "the create method of the journey gateway is called"
        1 * journeyGateway.create(_) >> { args ->
            args[0].id = 1
            args[0]
        }
    }

    def "Error while validating route"() {
        given: "a list representing an ordered route from Zaragoza to Granada containing one duplicated city"
        def route = [new City(1, "Zaragoza"), new City(10, "Madrid"), new City(3, "Malaga"), new City(10, "Madrid"), new City(7, "Granada")]
        and: "and a journey time of 26h 30m"
        Duration journeyTime = Duration.ofMinutes(1590)
        and: "and the validate route returns an error"
        validateRoute.execute(_) >> {throw new BusinessException(ROUTE_WITH_DUPLICATED_CITY, 10)}

        when: "the journey is creation is executed"
        createJourney.execute(route, journeyTime)

        then: "an exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == ROUTE_WITH_DUPLICATED_CITY.errorCode
        be.getMessage() == ROUTE_WITH_DUPLICATED_CITY.message
        be.params == 10
        and: "the create method of the journey gateway is not called"
        0 * journeyGateway.create(_)
    }

    def "Journey already existent"() {
        given: "a list representing an ordered route from Zaragoza to Granada containing one duplicated city"
        def route = [new City(1, "Zaragoza"), new City(10, "Madrid"), new City(3, "Malaga"), new City(10, "Madrid"), new City(7, "Granada")]
        and: "and a journey time of 26h 30m"
        Duration journeyTime = Duration.ofMinutes(1590)
        and: "and a journey from the origin city to the destination city is already existent"
        journeyGateway.obtainByOriginAndDestination(_,_) >> new Journey()

        when: "the journey is creation is executed"
        createJourney.execute(route, journeyTime)

        then: "an exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == DESTINATION_EXISTENT.errorCode
        and: "the create method of the journey gateway is not called"
        0 * journeyGateway.create(_)
    }
}
