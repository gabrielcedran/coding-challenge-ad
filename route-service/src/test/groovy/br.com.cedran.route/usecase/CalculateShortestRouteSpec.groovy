package br.com.cedran.route.usecase

import br.com.cedran.route.gateway.CityGateway
import br.com.cedran.route.model.City
import br.com.cedran.route.model.Destination
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.time.Duration.ofHours

class CalculateShortestRouteSpec extends Specification {

    CalculateShortestRoute calculateShortestRoute
    CityGateway cityGateway = Mock()
    ExecutorService executorService = Executors.newFixedThreadPool(4)

    def setup() {
        calculateShortestRoute = new CalculateShortestRoute(cityGateway, executorService)
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
    def "Test shortest route from City 1 to City 10" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, new City(2, "City 2", null), ofHours(2)),
                 new Destination(101, new City(3, "City 3", null), ofHours(3)),
                 new Destination(102, new City(4, "City 4", null), ofHours(1)),
                 new Destination(103, new City(5, "City 5", null), ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, new City(6, "City 6", null), ofHours(5)),
                 new Destination(105, new City(13, "City 13", null), ofHours(1)),
                 new Destination(106, new City(1, "City 1", null), ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, new City(7, "City 7", null), ofHours(2)),
                 new Destination(108, new City(1, "City 1", null), ofHours(3)),
                 new Destination(109, new City(13, "City 13", null), ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, new City(11, "City 11", null), ofHours(1)),
                 new Destination(111, new City(1, "City 1", null), ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, new City(8, "City 8", null), ofHours(1)),
                 new Destination(113, new City(1, "City 1", null), ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, new City(7, "City 7", null), ofHours(1)),
                 new Destination(115, new City(2, "City 2", null), ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, new City(3, "City 3", null), ofHours(2)),
                 new Destination(117, new City(9, "City 9", null), ofHours(2)),
                 new Destination(118, new City(10, "City 10", null), ofHours(2)),
                 new Destination(119, new City(6, "City 6", null), ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, new City( 5, "City 5", null), ofHours(1)),
                 new Destination(121, new City(9, "City 9", null), ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, new City(7, "City 7", null), ofHours(2)),
                 new Destination(123, new City(8, "City 8", null), ofHours(2)),
                 new Destination(124, new City(12, "City 12", null), ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, new City(7, "City 7", null), ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, new City(4, "City 4", null), ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, new City(9, "City 9", null), ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, new City(2, "City 2", null), ofHours(1)),
                 new Destination(129, new City(3, "City 3", null), ofHours(1))])

        when: "the shortest route between city 1 and city 10 is calculated"
        List<City> shortestTime = calculateShortestRoute.execute(1, 10)

        then: "the shortest route found contains 3 connections"
        shortestTime.size() == 3
        and: "the fastest route is city 5 -> city 3 -> city 7 -> city 10"
        shortestTime[0].id == 3
        shortestTime[1].id == 7
        shortestTime[2].id == 10
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
        1 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is called 1 time for city 7"
        1 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is not called for city 8"
        1 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is not called for city 9"
        0 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called for city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is not called for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is not called for city 12"
        0 * cityGateway.obtainWithDestinationsById(12) >> city12
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
    def "Test shortest route from City 1 to City 5" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, new City(2, "City 2", null), ofHours(2)),
                 new Destination(101, new City(3, "City 3", null), ofHours(3)),
                 new Destination(102, new City(4, "City 4", null), ofHours(1)),
                 new Destination(103, new City(5, "City 5", null), ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, new City(6, "City 6", null), ofHours(5)),
                 new Destination(105, new City(13, "City 13", null), ofHours(1)),
                 new Destination(106, new City(1, "City 1", null), ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, new City(7, "City 7", null), ofHours(2)),
                 new Destination(108, new City(1, "City 1", null), ofHours(3)),
                 new Destination(109, new City(13, "City 13", null), ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, new City(11, "City 11", null), ofHours(1)),
                 new Destination(111, new City(1, "City 1", null), ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, new City(8, "City 8", null), ofHours(1)),
                 new Destination(113, new City(1, "City 1", null), ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, new City(7, "City 7", null), ofHours(1)),
                 new Destination(115, new City(2, "City 2", null), ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, new City(3, "City 3", null), ofHours(2)),
                 new Destination(117, new City(9, "City 9", null), ofHours(2)),
                 new Destination(118, new City(10, "City 10", null), ofHours(2)),
                 new Destination(119, new City(6, "City 6", null), ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, new City( 5, "City 5", null), ofHours(1)),
                 new Destination(121, new City(9, "City 9", null), ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, new City(7, "City 7", null), ofHours(2)),
                 new Destination(123, new City(8, "City 8", null), ofHours(2)),
                 new Destination(124, new City(12, "City 12", null), ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, new City(7, "City 7", null), ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, new City(4, "City 4", null), ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, new City(9, "City 9", null), ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, new City(2, "City 2", null), ofHours(1)),
                 new Destination(129, new City(3, "City 3", null), ofHours(1))])

        when: "the shortest route between city 1 and city 5 is calculated"
        List<City> shortestTime = calculateShortestRoute.execute(1, 5)

        then: "the fastest route found contains 1 connections"
        shortestTime.size() == 1
        and: "the fastest route is city 1 -> city 5"
        shortestTime[0].id == 5
        and: "the city gateway is called 1 time for city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is not called for city 2"
        0 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is not called for city 3"
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is not called for city 4"
        0 * cityGateway.obtainWithDestinationsById(4) >> city4
        and: "the city gateway is not called for city 5"
        0 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is not called for city 6"
        0 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is not called for city 7"
        0 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is not called for city 8"
        0 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is not called for city 9"
        0 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is not called for city 11"
        0 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is not called city 12"
        0 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is not called city 13"
        0 * cityGateway.obtainWithDestinationsById(13) >> city13

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
    def "Test shortest route from City 11 to City 5" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, new City(2, "City 2", null), ofHours(2)),
                 new Destination(101, new City(3, "City 3", null), ofHours(3)),
                 new Destination(102, new City(4, "City 4", null), ofHours(1)),
                 new Destination(103, new City(5, "City 5", null), ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, new City(6, "City 6", null), ofHours(5)),
                 new Destination(105, new City(13, "City 13", null), ofHours(1)),
                 new Destination(106, new City(1, "City 1", null), ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, new City(7, "City 7", null), ofHours(2)),
                 new Destination(108, new City(1, "City 1", null), ofHours(3)),
                 new Destination(109, new City(13, "City 13", null), ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, new City(11, "City 11", null), ofHours(1)),
                 new Destination(111, new City(1, "City 1", null), ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, new City(8, "City 8", null), ofHours(1)),
                 new Destination(113, new City(1, "City 1", null), ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, new City(7, "City 7", null), ofHours(1)),
                 new Destination(115, new City(2, "City 2", null), ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, new City(3, "City 3", null), ofHours(2)),
                 new Destination(117, new City(9, "City 9", null), ofHours(2)),
                 new Destination(118, new City(10, "City 10", null), ofHours(2)),
                 new Destination(119, new City(6, "City 6", null), ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, new City( 5, "City 5", null), ofHours(1)),
                 new Destination(121, new City(9, "City 9", null), ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, new City(7, "City 7", null), ofHours(2)),
                 new Destination(123, new City(8, "City 8", null), ofHours(2)),
                 new Destination(124, new City(12, "City 12", null), ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, new City(7, "City 7", null), ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, new City(4, "City 4", null), ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, new City(9, "City 9", null), ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, new City(2, "City 2", null), ofHours(1)),
                 new Destination(129, new City(3, "City 3", null), ofHours(1))])

        when: "the shortest route between city 11 and city 5 is calculated"
        List<City> shortestTime = calculateShortestRoute.execute(11, 5)

        then: "the fastest route found contains 3 connections"
        shortestTime.size() == 3
        and: "the fastest route is city 11 -> city 4 -> city 1 -> city 5"
        shortestTime[0].id == 4
        shortestTime[1].id == 1
        shortestTime[2].id == 5
        and: "the city gateway is called 1 time for city 1"
        1 * cityGateway.obtainWithDestinationsById(1) >> city1
        and: "the city gateway is not called for city 2"
        0 * cityGateway.obtainWithDestinationsById(2) >> city2
        and: "the city gateway is not called for city 3"
        0 * cityGateway.obtainWithDestinationsById(3) >> city3
        and: "the city gateway is called 1 time for city 4"
        1 * cityGateway.obtainWithDestinationsById(4) >> city4
        and: "the city gateway is not called for city 5"
        0 * cityGateway.obtainWithDestinationsById(5) >> city5
        and: "the city gateway is not called for city 6"
        0 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is not called for city 7"
        0 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is not called for city 8"
        0 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is not called for city 9"
        0 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called for city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is called 1 time for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is not called for city 12"
        0 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is not called for city 13"
        0 * cityGateway.obtainWithDestinationsById(13) >> city13

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
    def "Test shortest route from City 11 to City 8" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, new City(2, "City 2", null), ofHours(2)),
                 new Destination(101, new City(3, "City 3", null), ofHours(3)),
                 new Destination(102, new City(4, "City 4", null), ofHours(1)),
                 new Destination(103, new City(5, "City 5", null), ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, new City(6, "City 6", null), ofHours(5)),
                 new Destination(105, new City(13, "City 13", null), ofHours(1)),
                 new Destination(106, new City(1, "City 1", null), ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, new City(7, "City 7", null), ofHours(2)),
                 new Destination(108, new City(1, "City 1", null), ofHours(3)),
                 new Destination(109, new City(13, "City 13", null), ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, new City(11, "City 11", null), ofHours(1)),
                 new Destination(111, new City(1, "City 1", null), ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, new City(8, "City 8", null), ofHours(1)),
                 new Destination(113, new City(1, "City 1", null), ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, new City(7, "City 7", null), ofHours(1)),
                 new Destination(115, new City(2, "City 2", null), ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, new City(3, "City 3", null), ofHours(2)),
                 new Destination(117, new City(9, "City 9", null), ofHours(2)),
                 new Destination(118, new City(10, "City 10", null), ofHours(2)),
                 new Destination(119, new City(6, "City 6", null), ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, new City( 5, "City 5", null), ofHours(1)),
                 new Destination(121, new City(9, "City 9", null), ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, new City(7, "City 7", null), ofHours(2)),
                 new Destination(123, new City(8, "City 8", null), ofHours(2)),
                 new Destination(124, new City(12, "City 12", null), ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, new City(7, "City 7", null), ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, new City(4, "City 4", null), ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, new City(9, "City 9", null), ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, new City(2, "City 2", null), ofHours(1)),
                 new Destination(129, new City(3, "City 3", null), ofHours(1))])

        when: "the shortest route between city 11 and city 8 is calculated"
        List<City> shortestTime = calculateShortestRoute.execute(11, 8)

        then: "the fastest route found contains 3 connections"
        shortestTime.size() == 4
        and: "the fastest route is city 11 -> city 4 -> city 1 -> city 5 -> city 8"
        shortestTime[0].id == 4
        shortestTime[1].id == 1
        shortestTime[2].id == 5
        shortestTime[3].id == 8
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
        and: "the city gateway is not called for city 6"
        0 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is not called for city 7"
        0 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is not called for city 8"
        0 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is not called for city 9"
        0 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is not called city 10"
        0 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is called 1 time for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is not called for city 12"
        0 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is not called for city 13"
        0 * cityGateway.obtainWithDestinationsById(13) >> city13

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
     * City 14:
     */
    def "Test shortest route between two cities without connection" () {
        given: "the city 1 destinations"
        City city1 = new City(1, "City 1",
                [new Destination(100, new City(2, "City 2", null), ofHours(2)),
                 new Destination(101, new City(3, "City 3", null), ofHours(3)),
                 new Destination(102, new City(4, "City 4", null), ofHours(1)),
                 new Destination(103, new City(5, "City 5", null), ofHours(1))])
        and: "the city 2 destinations"
        City city2 = new City(2, "City 2",
                [new Destination(104, new City(6, "City 6", null), ofHours(5)),
                 new Destination(105, new City(13, "City 13", null), ofHours(1)),
                 new Destination(106, new City(1, "City 1", null), ofHours(2))])
        and: "the city 3 destinations"
        City city3 = new City(3, "City 3",
                [new Destination(107, new City(7, "City 7", null), ofHours(2)),
                 new Destination(108, new City(1, "City 1", null), ofHours(3)),
                 new Destination(109, new City(13, "City 13", null), ofHours(1))])
        and: "the city 4 destinations"
        City city4 = new City(4, "City 4",
                [new Destination(110, new City(11, "City 11", null), ofHours(1)),
                 new Destination(111, new City(1, "City 1", null), ofHours(1))])
        and: "the city 5 destinations"
        City city5 = new City(5, "City 5",
                [new Destination(112, new City(8, "City 8", null), ofHours(1)),
                 new Destination(113, new City(1, "City 1", null), ofHours(1))])
        and: "the city 6 destinations"
        City city6 = new City(6, "City 6",
                [new Destination(114, new City(7, "City 7", null), ofHours(1)),
                 new Destination(115, new City(2, "City 2", null), ofHours(5))])
        and: "the city 7 destinations"
        City city7 = new City(7, "City 7",
                [new Destination(116, new City(3, "City 3", null), ofHours(2)),
                 new Destination(117, new City(9, "City 9", null), ofHours(2)),
                 new Destination(118, new City(10, "City 10", null), ofHours(2)),
                 new Destination(119, new City(6, "City 6", null), ofHours(1))])
        and: "the city 8 destinations"
        City city8 = new City(8, "City 8",
                [new Destination(120, new City( 5, "City 5", null), ofHours(1)),
                 new Destination(121, new City(9, "City 9", null), ofHours(2))])
        and: "the city 9 destinations"
        City city9 = new City(9, "City 9",
                [new Destination(122, new City(7, "City 7", null), ofHours(2)),
                 new Destination(123, new City(8, "City 8", null), ofHours(2)),
                 new Destination(124, new City(12, "City 12", null), ofHours(1))])
        and: "the city 10 destinations"
        City city10 = new City(10, "City 10",
                [new Destination(125, new City(7, "City 7", null), ofHours(2))])
        and: "the city 11 destinations"
        City city11 = new City(11, "City 11",
                [new Destination(126, new City(4, "City 4", null), ofHours(1))])
        and: "the city 12 destinations"
        City city12 = new City(12, "City 12",
                [new Destination(127, new City(9, "City 9", null), ofHours(1))])
        and: "the city 13 destinations"
        City city13 = new City(13, "City 13",
                [new Destination(128, new City(2, "City 2", null), ofHours(1)),
                 new Destination(129, new City(3, "City 3", null), ofHours(1))])

        City city14 = new City(13, "City 14", [])

        when: "the shortest route between city 11 and city 8 is calculated"
        List<City> shortestTime = calculateShortestRoute.execute(11, 14)

        then: "a null value is returned"
        shortestTime == null
        and: "the city gateway is called 1 time for city 2"
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
        1 * cityGateway.obtainWithDestinationsById(6) >> city6
        and: "the city gateway is called 1 time  for city 7"
        1 * cityGateway.obtainWithDestinationsById(7) >> city7
        and: "the city gateway is called 1 time for city 8"
        1 * cityGateway.obtainWithDestinationsById(8) >> city8
        and: "the city gateway is called 1 time for city 9"
        1 * cityGateway.obtainWithDestinationsById(9) >> city9
        and: "the city gateway is called 1 time for city 10"
        1 * cityGateway.obtainWithDestinationsById(10) >> city10
        and: "the city gateway is called 1 time for city 11"
        1 * cityGateway.obtainWithDestinationsById(11) >> city11
        and: "the city gateway is called 1 time for city 12"
        1 * cityGateway.obtainWithDestinationsById(12) >> city12
        and: "the city gateway is called 1 time for city 13"
        1 * cityGateway.obtainWithDestinationsById(13) >> city13

    }
}
