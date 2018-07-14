package br.com.cedran.route.usecase

import br.com.cedran.route.gateway.CityGateway
import br.com.cedran.route.model.City
import br.com.cedran.route.model.Destination
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification

import java.time.Duration

import static java.time.Duration.ofHours

class CalculateShortestRouteSpec extends Specification {

    CalculateShortestRoute calculateShortestRoute
    CityGateway cityGateway = Mock()

    def setup() {
        calculateShortestRoute = new CalculateShortestRoute(cityGateway)
    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 10
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "test" () {
        given: ""
        City city1 = new City(1, "City 1",
                [new Destination(2, ofHours(1)),
                 new Destination(3, ofHours(6)),
                 new Destination(4, ofHours(10))])
        cityGateway.obtainById(1) >> city1

        City city2 = new City(2, "City 2",
                [new Destination(4, ofHours(7)),
                 new Destination(5, ofHours(2))])
        cityGateway.obtainById(2) >> city2

        City city3 = new City(3, "City 3",
                [new Destination(6, ofHours(2))])
        cityGateway.obtainById(3) >> city3

        City city4 = new City(4, "City 4",
                [new Destination(1, ofHours(9))])
        cityGateway.obtainById(4) >> city4

        City city5 = new City(5, "City 5",
                [new Destination(6, ofHours(1))])
        cityGateway.obtainById(5) >> city5

        City city6 = new City(6, "City 6",
                [new Destination(4, ofHours(2))])
        cityGateway.obtainById(6) >> city6

        when: ""
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: ""
        shortestTime.left == 21600
        shortestTime.right[0] == 2
        shortestTime.right[1] == 5
        shortestTime.right[2] == 6
        shortestTime.right[3] == 4

    }

    /**
     * City 1: City 2 - 2 | City 3 - 6 | City 4 - 1
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "test 2" () {
        given: ""
        City city1 = new City(1, "City 1",
                [new Destination(2, ofHours(2)),
                 new Destination(3, ofHours(6)),
                 new Destination(4, ofHours(1))])
        cityGateway.obtainById(1) >> city1

        City city2 = new City(2, "City 2",
                [new Destination(4, ofHours(7)),
                 new Destination(5, ofHours(2))])
        cityGateway.obtainById(2) >> city2

        City city3 = new City(3, "City 3",
                [new Destination(6, ofHours(2))])
        cityGateway.obtainById(3) >> city3

        City city4 = new City(4, "City 4",
                [new Destination(1, ofHours(9))])
        cityGateway.obtainById(4) >> city4

        City city5 = new City(5, "City 5",
                [new Destination(6, ofHours(1))])
        cityGateway.obtainById(5) >> city5

        City city6 = new City(6, "City 6",
                [new Destination(4, ofHours(2))])
        cityGateway.obtainById(6) >> city6

        when: ""
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: ""
        shortestTime.left == 3600
        shortestTime.right[0] == 4

    }


    /**
     * City 1: City 2 - 2 | City 3 - 3 | City 4 - 1 | City 5 - 1
     * City 2: City 6 - 5 | City 13 - 1 | City 1 - 2
     * City 3: City 7 - 2 | City 1 - 3 | City 13 - 1
     * City 4: City 11 - 1 | City 1 - 1
     * City 5: City 8 - 1 | City 1 - 1
     * City 6: City 7 - 1 | City 2 - 5
     * City 7: City 3 - 2 | City 9 - 2 | City 10 - 2 | City 6 - 1
     * City 8: City 5 - 1 | City 9 - 2
     * City 9: City 7 - 2 | City 8 - 2 | City 12 - 1
     * City 10: City 7 - 2
     * City 11: City 4 - 1
     * City 12: City 9 - 1
     * City 13: City 2 - 1 | City 3 - 1
     */
    def "test 3" () {
        given: ""
        City city1 = new City(1, "City 1",
                [new Destination(2, ofHours(2)),
                 new Destination(3, ofHours(3)),
                 new Destination(4, ofHours(1)),
                 new Destination(5, ofHours(1))])
        cityGateway.obtainById(1) >> city1

        City city2 = new City(2, "City 2",
                [new Destination(6, ofHours(5)),
                 new Destination(13, ofHours(1)),
                 new Destination(1, ofHours(2))])
        cityGateway.obtainById(2) >> city2

        City city3 = new City(3, "City 3",
                [new Destination(7, ofHours(2)),
                 new Destination(1, ofHours(3)),
                 new Destination(13, ofHours(1))])
        cityGateway.obtainById(3) >> city3

        City city4 = new City(4, "City 4",
                [new Destination(11, ofHours(1)),
                 new Destination(1, ofHours(1))])
        cityGateway.obtainById(4) >> city4

        City city5 = new City(5, "City 5",
                [new Destination(8, ofHours(1)),
                 new Destination(1, ofHours(1))])
        cityGateway.obtainById(5) >> city5

        City city6 = new City(6, "City 6",
                [new Destination(7, ofHours(1)),
                 new Destination(2, ofHours(5))])
        cityGateway.obtainById(6) >> city6

        City city7 = new City(7, "City 6",
                [new Destination(3, ofHours(2)),
                 new Destination(9, ofHours(2)),
                 new Destination(10, ofHours(2)),
                 new Destination(6, ofHours(1))])
        cityGateway.obtainById(7) >> city7

        City city8 = new City(8, "City 8",
                [new Destination(5, ofHours(1)),
                 new Destination(9, ofHours(2))])
        cityGateway.obtainById(8) >> city8

        City city9 = new City(9, "City 9",
                [new Destination(7, ofHours(2)),
                 new Destination(8, ofHours(2)),
                 new Destination(12, ofHours(1))])
        cityGateway.obtainById(9) >> city9

        City city10 = new City(10, "City 10",
                [new Destination(7, ofHours(2))])
        cityGateway.obtainById(10) >> city10

        City city11 = new City(11, "City 11",
                [new Destination(4, ofHours(1))])
        cityGateway.obtainById(11) >> city11

        City city12 = new City(12, "City 12",
                [new Destination(9, ofHours(1))])
        cityGateway.obtainById(12) >> city12

        City city13 = new City(13, "City 13",
                [new Destination(2, ofHours(1)),
                 new Destination(3, ofHours(1))])
        cityGateway.obtainById(13) >> city13

        when: ""
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 10)

        then: ""
        shortestTime.left == 25200
        shortestTime.right[0] == 3
        shortestTime.right[1] == 7
        shortestTime.right[2] == 10

    }

    /**
     * City 1: City 2 - 2 | City 3 - 3 | City 4 - 1 | City 5 - 1
     * City 2: City 6 - 5 | City 13 - 1 | City 1 - 2
     * City 3: City 7 - 2 | City 1 - 3 | City 13 - 1
     * City 4: City 11 - 1 | City 1 - 1
     * City 5: City 8 - 1 | City 1 - 1
     * City 6: City 7 - 1 | City 2 - 5
     * City 7: City 3 - 2 | City 9 - 2 | City 10 - 2 | City 6 - 1
     * City 8: City 5 - 1 | City 9 - 2
     * City 9: City 7 - 2 | City 8 - 2 | City 12 - 1
     * City 10: City 7 - 2
     * City 11: City 4 - 1
     * City 12: City 9 - 1
     * City 13: City 2 - 1 | City 3 - 1
     */
    def "test 4" () {
        given: ""
        City city1 = new City(1, "City 1",
                [new Destination(2, ofHours(2)),
                 new Destination(3, ofHours(3)),
                 new Destination(4, ofHours(1)),
                 new Destination(5, ofHours(1))])
        cityGateway.obtainById(1) >> city1

        City city2 = new City(2, "City 2",
                [new Destination(6, ofHours(5)),
                 new Destination(13, ofHours(1)),
                 new Destination(1, ofHours(2))])
        cityGateway.obtainById(2) >> city2

        City city3 = new City(3, "City 3",
                [new Destination(7, ofHours(2)),
                 new Destination(1, ofHours(3)),
                 new Destination(13, ofHours(1))])
        cityGateway.obtainById(3) >> city3

        City city4 = new City(4, "City 4",
                [new Destination(11, ofHours(1)),
                 new Destination(1, ofHours(1))])
        cityGateway.obtainById(4) >> city4

        City city5 = new City(5, "City 5",
                [new Destination(8, ofHours(1)),
                 new Destination(1, ofHours(1))])
        cityGateway.obtainById(5) >> city5

        City city6 = new City(6, "City 6",
                [new Destination(7, ofHours(1)),
                 new Destination(2, ofHours(5))])
        cityGateway.obtainById(6) >> city6

        City city7 = new City(7, "City 6",
                [new Destination(3, ofHours(2)),
                 new Destination(9, ofHours(2)),
                 new Destination(10, ofHours(2)),
                 new Destination(6, ofHours(1))])
        cityGateway.obtainById(7) >> city7

        City city8 = new City(8, "City 8",
                [new Destination(5, ofHours(1)),
                 new Destination(9, ofHours(2))])
        cityGateway.obtainById(8) >> city8

        City city9 = new City(9, "City 9",
                [new Destination(7, ofHours(2)),
                 new Destination(8, ofHours(2)),
                 new Destination(12, ofHours(1))])
        cityGateway.obtainById(9) >> city9

        City city10 = new City(10, "City 10",
                [new Destination(7, ofHours(2))])
        cityGateway.obtainById(10) >> city10

        City city11 = new City(11, "City 11",
                [new Destination(4, ofHours(1))])
        cityGateway.obtainById(11) >> city11

        City city12 = new City(12, "City 12",
                [new Destination(9, ofHours(1))])
        cityGateway.obtainById(12) >> city12

        City city13 = new City(13, "City 13",
                [new Destination(2, ofHours(1)),
                 new Destination(3, ofHours(1))])
        cityGateway.obtainById(13) >> city13

        when: ""
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(5, 12)

        then: ""
        shortestTime.left == 14400
        shortestTime.right[0] == 8
        shortestTime.right[1] == 9
        shortestTime.right[2] == 12

    }

}
