package br.com.cedran.route.gateway.web;

import br.com.cedran.route.model.City;
import br.com.cedran.route.usecase.CalculateFastestRoute;
import br.com.cedran.route.usecase.CalculateShortestRoute;
import br.com.cedran.route.usecase.exceptions.BusinessException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static br.com.cedran.route.model.ErrorCode.ORIGIN_AND_DESTINATION_THE_SAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RouteControllerTest {

    @Value("http://localhost:${local.server.port}/v1/route")
    private String serverUrl;

    @MockBean
    private CalculateFastestRoute calculateFastestRoute;

    @MockBean
    private CalculateShortestRoute calculateShortestRoute;

    @Test
    public void test_find_fastest_and_shortest_route_successfully() {

        City granada = new City(2L, "Granada", null);
        City malaga = new City(4L, "Malaga", null);
        City madrid = new City(5L, "Madrid", null);

        Pair<Duration, List<City>> fastestRoute = Pair.of(Duration.ofMinutes(150), Arrays.asList(granada, malaga, madrid));

        Mockito.when(calculateFastestRoute.execute(1L, 5L)).thenReturn(fastestRoute);

        List<City> shortestRoute = Arrays.asList(granada, madrid);
        Mockito.when(calculateShortestRoute.execute(1L, 5L)).thenReturn(shortestRoute);

        given()
            .pathParam("originCityId", "1")
            .pathParam("destinationCityId", "5")
        .when()
            .get(serverUrl+"/{originCityId}/{destinationCityId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("fastest.cities.id", contains(2, 4, 5))
            .body("fastest.cities.name", contains("Granada", "Malaga", "Madrid"))
            .body("fastest.journeyTimeInMinutes", equalTo(150))
            .body("shortest.cities.id", contains(2, 5))
            .body("shortest.cities.name", contains("Granada", "Madrid"))
            .body("shortest.numberOfStops", equalTo(2))
        ;
    }

    @Test
    public void test_find_fastest_route_unexpected_error() {

        Mockito.when(calculateFastestRoute.execute(1L, 5L)).thenThrow(new RuntimeException("Unexpected error"));

        given()
            .pathParam("originCityId", "1")
            .pathParam("destinationCityId", "5")
        .when()
            .get(serverUrl+"/{originCityId}/{destinationCityId}")
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("errorCode", equalTo("GE00001"))
            .body("errorMessage", equalTo("Unexpected error"))
        ;
    }

    @Test
    public void test_find_fastest_route_business_exception() {

        Mockito.when(calculateFastestRoute.execute(1L, 1L)).thenThrow(new BusinessException(ORIGIN_AND_DESTINATION_THE_SAME, 1L));

        given()
            .pathParam("originCityId", "1")
            .pathParam("destinationCityId", "1")
        .when()
            .get(serverUrl+"/{originCityId}/{destinationCityId}")
        .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("errorCode", equalTo("BE00007"))
            .body("errorMessage", equalTo("The origin and destination are the same"))
            .body("params", equalTo(1))
        ;
    }

    @Test
    public void test_find_fastest_route_without_result() {

        Mockito.when(calculateFastestRoute.execute(1L, 5L)).thenReturn(null);

        Mockito.when(calculateShortestRoute.execute(1L, 5L)).thenReturn(null);

        given()
            .pathParam("originCityId", "1")
            .pathParam("destinationCityId", "5")
        .when()
            .get(serverUrl+"/{originCityId}/{destinationCityId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("fastest", nullValue())
            .body("shortest", nullValue())
        ;
    }
}
