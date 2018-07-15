package br.com.cedran.city.gateway.web;

import br.com.cedran.city.gateway.web.mvc.dto.DestinationRequestDTO;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import br.com.cedran.city.usecase.*;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Configuration
public class DestinationControllerTest {

    @MockBean
    private AddDestinationToCity addDestinationToCity;

    @MockBean
    private RemoveDestinationFromCity removeDestinationFromCity;

    @Value("http://localhost:${local.server.port}/v1/city/{cityId}/destination")
    private String serverUrl;

    @Test
    public void test_create_destination_successfully() {

        City zaragoza = new City(1L, "Zaragoza", null);
        City granada = new City(2L, "Granada", null);
        Destination destination = new Destination(100L, zaragoza, granada, Duration.ofMinutes(90));

        Mockito.when(addDestinationToCity.execute(1L, 2L, Duration.ofMinutes(90))).thenReturn(destination);

        DestinationRequestDTO requestDTO = new DestinationRequestDTO();
        requestDTO.setDestinationCityId(2L);
        requestDTO.setJourneyTimeInMinutes(90L);

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .pathParam("cityId", "1")
            .body(requestDTO)
        .when()
            .post(serverUrl)
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", equalTo(100))
            .body("city.id", equalTo(2))
            .body("city.name", equalTo("Granada"))
            .body("journeyTimeInMinutes", equalTo(90))
        ;

    }

    @Test
    public void test_create_destination_without_mandatory_fields() {

        DestinationRequestDTO requestDTO = new DestinationRequestDTO();

        given()
            .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
            .pathParam("cityId", "1")
            .body(requestDTO)
        .when()
            .post(serverUrl)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorCode", equalTo("BE00006"))
                .body("params", containsInAnyOrder("journeyTimeInMinutes must not be null", "destinationCityId must not be null"))
                .body("errorMessage", equalTo("Invalid argument"))
        ;

    }


    @Test
    public void test_delete_destination() {


        Mockito.doNothing().when(removeDestinationFromCity).execute(1L, 2L);

        given()
            .pathParam("cityId", "1")
            .pathParam("destinationId", "2")
        .when()
            .delete(serverUrl+"/{destinationId}")
        .then()
            .statusCode(HttpStatus.OK.value())
        ;

    }
}
