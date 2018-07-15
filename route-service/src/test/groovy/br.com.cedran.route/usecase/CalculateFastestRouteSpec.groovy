package br.com.cedran.route.usecase

import br.com.cedran.route.gateway.CityGateway
import br.com.cedran.route.model.City
import br.com.cedran.route.model.Destination
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification

import static java.time.Duration.ofHours

class CalculateFastestRouteSpec extends Specification {

    CalculateFastestRoute calculateShortestRoute
    CityGateway cityGateway = Mock()

    def setup() {
        calculateShortestRoute = new CalculateFastestRoute(cityGateway)
    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 10
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "Test processing after finding the destination in the first step but with the fastest route being the one with more connection" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, 2, "City 2", ofHours(1)),
                 new Destination(101, 3, "City 3", ofHours(6)),
                 new Destination(102, 4, "City 4", ofHours(10))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103, 4, "City 4", ofHours(7)),
                 new Destination(104, 5, "City 5", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105, 6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106, 1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107, 6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108, 4, "City 4", ofHours(2))])

        when: "the fastest between city 1 and city 4 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: "the fastest route found is in 6 hour (21600 seconds)"
        shortestTime.left == 21600
        and: "the route is city 1 -> city 5 -> city 6 -> city 4"
        shortestTime.right[0] == 2
        shortestTime.right[1] == 5
        shortestTime.right[2] == 6
        shortestTime.right[3] == 4
        and: "the city gateway is called 1 time to fetch the city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time to fetch the city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is called 1 time to fetch the city 5"
        1 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is called 1 time to fetch the city 6"
        1 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is is not called for the other cities"
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        0 * cityGateway.obtainWithDestinationsById(4) >> city4

    }

    /**
     * City 1: City 2 - 2 | City 3 - 1 | City 4 - 10
     * City 2: City 4 - 7 | City 5 - 4
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "Test processing after finding the destination in the first step but with the fastest route being median in the number of connections" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100,2, "City 2", ofHours(2)),
                 new Destination(101,3, "City 3", ofHours(1)),
                 new Destination(102,4, "City 4", ofHours(10))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103,4, "City 4", ofHours(7)),
                 new Destination(104,5, "City 5", ofHours(4))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105,6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106,1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107,6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108,4, "City 4", ofHours(2))])

        when: "the fastest between city 1 and city 4 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: "the fastest route found is in 6 hour (21600 seconds)"
        shortestTime.left == 18000
        and: "the route is city 1 -> city 3 -> city 6 -> city 4"
        shortestTime.right[0] == 3
        shortestTime.right[1] == 6
        shortestTime.right[2] == 4
        and: "the city gateway is called 1 time to fetch the city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time to fetch the city 3"
        1 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is called 1 time to fetch the city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is called 1 time to fetch the city 6"
        1 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is is not called for the other cities"
        0 * cityGateway.obtainWithDestinationsById(4) >> city4
        0 * cityGateway.obtainWithDestinationsById(5) >> city5

    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 1
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "Test processing stops after destination is found in the first step and there is no chance of faster route since the beginning" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100,2, "City 2", ofHours(1)),
                 new Destination(101,3, "City 3", ofHours(6)),
                 new Destination(102,4, "City 4", ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103,4, "City 4", ofHours(7)),
                 new Destination(104,5, "City 5", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105,6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106,1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107,6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108,4, "City 4", ofHours(2))])

        when: "the fastest between city 1 and city 4 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: "the fastest route found is in 1 hour (3600 seconds)"
        shortestTime.left == 3600
        and: "the route is city 1 to city 4 directly"
        shortestTime.right[0] == 4
        and: "the city gateway is called 1 time to fetch the city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the other city gateways are never called"
        0 * cityGateway.obtainWithDestinationsById(2) >> city2
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        0 * cityGateway.obtainWithDestinationsById(4) >> city4
        0 * cityGateway.obtainWithDestinationsById(5) >> city5
        0 * cityGateway.obtainWithDestinationsById(6) >> city6

    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 2
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     */
    def "Test processing stops after destination is found in the first step and there is no better options after the second round of processing " () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100,2, "City 2", ofHours(1)),
                 new Destination(101,3, "City 3", ofHours(6)),
                 new Destination(102,4, "City 4", ofHours(2))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103,4, "City 4", ofHours(7)),
                 new Destination(104,5, "City 5", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105,6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106,1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107,6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108,4, "City 4", ofHours(2))])

        when: "the fastest between city 1 and city 4 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 4)

        then: "the fastest route found is in 2 hours (7200 seconds)"
        shortestTime.left == 7200
        and: "the route is city 1 to city 4 directly"
        shortestTime.right[0] == 4
        and: "the city gateway is called 1 time to fetch the city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time to fetch the city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the other city gateways are never called"
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        0 * cityGateway.obtainWithDestinationsById(4) >> city4
        0 * cityGateway.obtainWithDestinationsById(5) >> city5
        0 * cityGateway.obtainWithDestinationsById(6) >> city6

    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 10
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     * City 7: -
     */
    def "Test two cities without connection" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, 2, "City 2", ofHours(1)),
                 new Destination(101, 3, "City 3", ofHours(6)),
                 new Destination(102,4, "City 4", ofHours(10))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103, 4, "City 4", ofHours(7)),
                 new Destination(104,5, "City 5", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105,6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106,1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107,6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108,4, "City 4", ofHours(2))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7", null)

        when: "the fastest between city 1 and city 7 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 7)

        then: "no route is found"
        shortestTime == null
        and: "the city gateway is called 1 time to fetch the city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time to fetch the city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is called 1 time to fetch the city 3"
        1 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is called 1 time to fetch the city 4"
        1 * cityGateway.obtainWithDestinationsById(4) >> city4
        and: "the city gateway is called 1 time to fetch the city 5"
        1 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is called 1 time to fetch the city 6"
        1 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is called never called for the city 7"
        0 * cityGateway.obtainWithDestinationsById(7) >> city7

    }

    /**
     * City 1: City 2 - 1 | City 3 - 6 | City 4 - 10
     * City 2: City 4 - 7 | City 5 - 2
     * City 3: City 6 - 2
     * City 5: City 6 - 1
     * City 6: City 4 - 2
     * City 7: -
     */
    def "Test origin city without any connection" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100,2, "City 2", ofHours(1)),
                 new Destination(101,3, "City 3", ofHours(6)),
                 new Destination(102,4, "City 4", ofHours(10))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(103,4, "City 4", ofHours(7)),
                 new Destination(104,5, "City 5", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(105,6, "City 6", ofHours(2))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(106,1, "City 1", ofHours(9))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(107,6, "City 6", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(108,4, "City 4", ofHours(2))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7", null)

        when: "the fastest between city 7 and city 1 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(7, 1)

        then: "no route is found"
        shortestTime == null
        and: "the city gateway is called 1 time to fetch the city 7"
        1 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is never called for the other cities"
        0 * cityGateway.obtainWithDestinationsById(1) >> city1
        0 * cityGateway.obtainWithDestinationsById(2) >> city2
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        0 * cityGateway.obtainWithDestinationsById(4) >> city4
        0 * cityGateway.obtainWithDestinationsById(5) >> city5
        0 * cityGateway.obtainWithDestinationsById(6) >> city6

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
    def "Test fastest route is the one with smallest number of connection in a indirect weighted graph" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100,2, "City 2", ofHours(2)),
                 new Destination(101,3, "City 3", ofHours(3)),
                 new Destination(102,4, "City 4", ofHours(1)),
                 new Destination(103,5, "City 5", ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104,6, "City 6", ofHours(5)),
                 new Destination(105,13, "City 13", ofHours(1)),
                 new Destination(106,1, "City 1", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107,7, "City 7", ofHours(2)),
                 new Destination(108,1, "City 1", ofHours(3)),
                 new Destination(109,13, "City 13", ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110,11, "City 11", ofHours(1)),
                 new Destination(111,1, "City 1", ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112,8, "City 8", ofHours(1)),
                 new Destination(113,1, "City 1", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114,7, "City 7", ofHours(1)),
                 new Destination(115,2, "City 2", ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116,3, "City 3", ofHours(2)),
                 new Destination(117, 9, "City 9", ofHours(2)),
                 new Destination(118, 10, "City 10", ofHours(2)),
                 new Destination(119, 6, "City 6", ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, 5, "City 5", ofHours(1)),
                 new Destination(121, 9, "City 9", ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, 7, "City 7", ofHours(2)),
                 new Destination(123, 8, "City 8", ofHours(2)),
                 new Destination(124, 12, "City 12", ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125,7, "City 7", ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, 4, "City 4", ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, 9, "City 9", ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, 2, "City 2", ofHours(1)),
                 new Destination(129, 3, "City 3", ofHours(1))])

        when: "the fastest between city 1 and city 10 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(1, 10)

        then: "the fastest route found is in 7 hours (25200 seconds)"
        shortestTime.left == 25200
        and: "the fastest route is city 1 -> city 3 -> city 7 -> city 10"
        shortestTime.right[0] == 3
        shortestTime.right[1] == 7
        shortestTime.right[2] == 10
        and: "the city gateway is called 1 time for city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time for city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is called 1 time for city 3"
        1 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is called 1 time for city 4"
        1 * cityGateway.obtainWithDestinationsById(4) >> city4
        and: "the city gateway is called 1 time for city 5"
        1 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is called 1 time for city 6"
        0 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is called 1 time for city 7"
        1 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is called 1 time for city 8"
        1 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is called 1 time for city 9"
        1 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called for city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is called 1 time for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is called 1 time for city 12"
        1 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is called 1 time for city 13"
        1 * cityGateway.obtainWithDestinationsById(13) >> city13

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
    def "Test fastest route not passing in all connections in an indirect weighted graph" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, 2, "City 2", ofHours(2)),
                 new Destination(101, 3, "City 3", ofHours(3)),
                 new Destination(102, 4, "City 4", ofHours(1)),
                 new Destination(103, 5, "City 5", ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, 6, "City 6", ofHours(5)),
                 new Destination(105, 13, "City 13", ofHours(1)),
                 new Destination(106, 1, "City 1", ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, 7, "City 7", ofHours(2)),
                 new Destination(108, 1, "City 1", ofHours(3)),
                 new Destination(109, 13, "City 13", ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, 11, "City 11", ofHours(1)),
                 new Destination(111, 1, "City 1", ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, 8, "City 8", ofHours(1)),
                 new Destination(113, 1, "City 1", ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, 7, "City 7", ofHours(1)),
                 new Destination(115, 2, "City 2", ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, 3, "City 3", ofHours(2)),
                 new Destination(117, 9, "City 9", ofHours(2)),
                 new Destination(118, 10, "City 10", ofHours(2)),
                 new Destination(119, 6, "City 6", ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, 5, "City 5", ofHours(1)),
                 new Destination(121, 9, "City 9", ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, 7, "City 7", ofHours(2)),
                 new Destination(123, 8, "City 8", ofHours(2)),
                 new Destination(124, 12, "City 12", ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, 7, "City 7", ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, 4, "City 4", ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, 9, "City 9", ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, 2, "City 2", ofHours(1)),
                 new Destination(129, 3, "City 3", ofHours(1))])

        when: "the fastest between city 5 and city 12 is calculated"
        Pair<Long, List<Long>> shortestTime = calculateShortestRoute.execute(5, 12)

        then: "the fastest route found is in 4 hours (14400 seconds)"
        shortestTime.left == 14400
        and: "the fastest route is city 5 -> city 8 -> city 9 -> city 12"
        shortestTime.right[0] == 8
        shortestTime.right[1] == 9
        shortestTime.right[2] == 12
        and: "the city gateway is called 1 time for city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is called 1 time for city 2"
        1 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is not called for city 3"
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is called 1 time for city 4"
        1 * cityGateway.obtainWithDestinationsById(4) >> city4
        and: "the city gateway is called 1 time for city 5"
        1 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is not called for city 6"
        0 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is not called for city 7"
        0 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is called 1 time for city 8"
        1 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is called 1 time for city 9"
        1 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called for city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is called 1 time for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is not called for city 12"
        0 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is not called for city 13"
        0 * cityGateway.obtainWithDestinationsById(13) >> city13

    }

}
