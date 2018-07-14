package br.com.cedran.city

import br.com.cedran.city.gateway.DestinationGateway
import br.com.cedran.city.model.Destination
import br.com.cedran.city.usecase.RemoveDestinationFromCity
import br.com.cedran.city.usecase.exceptions.BusinessException
import spock.lang.Specification

import static br.com.cedran.city.model.ErrorCode.DESTINATION_NOT_EXISTENT

class RemoveDestinationFromCitySpec extends Specification {

    RemoveDestinationFromCity removeDestinationFromCity

    DestinationGateway destinationGateway = Mock()

    def setup() {
        removeDestinationFromCity = new RemoveDestinationFromCity(destinationGateway)
    }


    def "remove destination successfully"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "this destination exists"
        destinationGateway.obtainByOriginAndDestination(originCityId, destinationCityId) >> new Destination()

        when: "the add destination to city uc is executed"
        removeDestinationFromCity.execute(originCityId, destinationCityId)

        then: "the delete method of the destination gateway is called"
        1 * destinationGateway.delete(_)
    }

    def "remove non existent destination"() {
        given: "the origin city id"
        Long originCityId = 1
        and: "the destination city id"
        Long destinationCityId = 2
        and: "this destination do not exists"
        destinationGateway.obtainByOriginAndDestination(originCityId, destinationCityId) >> null

        when: "the add destination to city uc is executed"
        removeDestinationFromCity.execute(originCityId, destinationCityId)

        then: "the delete method of the destination gateway is not called"
        0 * destinationGateway.delete(_)
        and: "an exception is raised"
        BusinessException be = thrown(BusinessException)
        be.errorCode == DESTINATION_NOT_EXISTENT.errorCode
    }

}
