package br.com.cedran.city.gateway.web;

import br.com.cedran.city.gateway.web.dtos.CityRequestDTO;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import br.com.cedran.city.model.ErrorCode;
import br.com.cedran.city.usecase.CreateCity;
import br.com.cedran.city.usecase.ObtainCityWithDestinations;
import br.com.cedran.city.usecase.UpdateCity;
import br.com.cedran.city.usecase.exceptions.BusinessException;
import io.restassured.http.ContentType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Configuration
public class CityControllerTest {

    @MockBean
    private CreateCity createCity;

    @MockBean
    private UpdateCity updateCity;

    @MockBean
    private ObtainCityWithDestinations obtainCityWithDestinations;

    @Value("http://localhost:${local.server.port}/v1/city")
    private String serverUrl;

    @Test
    public void test_find_city_with_destinations_successfully() {

        City zaragoza = new City(1L, "Zaragoza", null);
        City granada = new City(2L, "Granada", null);
        City madrid = new City(3L, "Madrid", null);
        Destination destinationGranada = new Destination(10L, zaragoza, granada, Duration.ofHours(1));
        Destination destinationMadrid = new Destination(11L, zaragoza, madrid, Duration.ofHours(2));
        zaragoza.setDestinations(Arrays.asList(destinationGranada, destinationMadrid));

        Mockito.when(obtainCityWithDestinations.execute(1L)).thenReturn(zaragoza);

        given()
            .pathParam("cityId", "1")
        .when()
            .get(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(1))
            .body("name", equalTo("Zaragoza") )
            .body("destinations.size()", equalTo(2))
            .root("destinations.find {it.id == 10}")
            .body("originCity.id", equalTo(1))
            .body("originCity.name", equalTo("Zaragoza"))
            .body("originCity.destinations", nullValue())
            .body("destinationCity.id", equalTo(2))
            .body("destinationCity.name", equalTo("Granada"))
            .body("destinationCity.destinations", nullValue())
            .body("journeyTimeInMinutes", equalTo(60))
            .root("destinations.find {it.id == 11}")
            .body("originCity.id", equalTo(1))
            .body("originCity.name", equalTo("Zaragoza"))
            .body("originCity.destinations", nullValue())
            .body("destinationCity.id", equalTo(3))
            .body("destinationCity.name", equalTo("Madrid"))
            .body("destinationCity.destinations", nullValue())
            .body("journeyTimeInMinutes", equalTo(120))
            ;

    }

    @Test
    public void test_find_city_with_destinations_with_invalid_city_id() {

        Mockito.when(obtainCityWithDestinations.execute(1L)).thenThrow(new BusinessException(ErrorCode.CITY_NOT_EXISTENT, 1));

        given()
            .pathParam("cityId", "aaa")
        .when()
            .get(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("errorCode", equalTo("BE00006"))
            .body("errorMessage", equalTo("Invalid argument"))
        ;

    }

    @Test
    public void test_find_city_with_destinations_business_error() {

        Mockito.when(obtainCityWithDestinations.execute(1L)).thenThrow(new BusinessException(ErrorCode.CITY_NOT_EXISTENT, 1));

        given()
            .pathParam("cityId", "1")
        .when()
            .get(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("errorCode", equalTo("BE00002"))
            .body("errorMessage", equalTo("City not existent"))
            .body("params", equalTo(1))
        ;

    }

    @Test
    public void test_find_city_with_destinations_unexpected_error() {

        Mockito.when(obtainCityWithDestinations.execute(1L)).thenThrow(new RuntimeException(""));

        given()
            .pathParam("cityId", "1")
        .when()
            .get(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("errorCode", equalTo("GE00001"))
            .body("errorMessage", equalTo("Unexpected error"))
            .body("params", nullValue())
        ;

    }


    @Test
    public void test_create_city_successfully() {

        City city = new City();
        city.setId(100L);
        city.setName("Valencia");
        Mockito.when(createCity.execute(Mockito.anyString())).thenReturn(city);

        CityRequestDTO cityRequestDTO = new CityRequestDTO();
        cityRequestDTO.setName("Valencia");

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .body(cityRequestDTO)
        .when()
            .post(serverUrl+"")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", equalTo(100))
            .body("name", equalTo("Valencia"))
            .body("destinations", nullValue())
        ;

    }


    @Test
    public void test_create_city_without_name() {

        City city = new City();
        city.setId(100L);
        city.setName("Valencia");
        Mockito.when(createCity.execute(Mockito.anyString())).thenReturn(city);

        CityRequestDTO cityRequestDTO = new CityRequestDTO();

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .body(cityRequestDTO)
        .when()
            .post(serverUrl+"")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("errorCode", equalTo("BE00006"))
            .body("errorMessage", equalTo("Invalid argument"))
            .body("params", containsInAnyOrder("name must not be empty", "name must not be null"))
        ;

    }

    @Test
    public void test_update_city_successfully() {

        City city = new City();
        city.setId(100L);
        city.setName("Valencia");
        Mockito.when(updateCity.execute(Mockito.any(City.class))).thenReturn(city);

        CityRequestDTO cityRequestDTO = new CityRequestDTO();
        cityRequestDTO.setName("Valencia");

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .pathParam("cityId", "100")
            .body(cityRequestDTO)
        .when()
            .put(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(100))
            .body("name", equalTo("Valencia"))
            .body("destination", nullValue())
        ;

    }

    @Test
    public void test_update_city_without_name() {

        City city = new City();
        city.setId(100L);
        city.setName("Valencia");
        Mockito.when(updateCity.execute(Mockito.any(City.class))).thenReturn(city);

        CityRequestDTO cityRequestDTO = new CityRequestDTO();

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .pathParam("cityId", "100")
            .body(cityRequestDTO)
        .when()
            .put(serverUrl+"/{cityId}")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("errorCode", equalTo("BE00006"))
            .body("errorMessage", equalTo("Invalid argument"))
            .body("params", containsInAnyOrder("name must not be empty", "name must not be null"))
        ;

    }
}
