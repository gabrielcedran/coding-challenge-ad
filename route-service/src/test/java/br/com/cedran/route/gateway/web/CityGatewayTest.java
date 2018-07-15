package br.com.cedran.route.gateway.web;

import br.com.cedran.route.gateway.CityGateway;
import br.com.cedran.route.gateway.web.feign.dto.CityDTO;
import br.com.cedran.route.gateway.web.feign.dto.DestinationDTO;
import br.com.cedran.route.model.City;
import br.com.cedran.route.model.Destination;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CityGatewayTest {

    @Autowired
    private CityGateway cityGateway;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            wireMockConfig()
                    .port(9000)
                    .notifier(new Slf4jNotifier(true)));

    @Before
    public void setUp() {

        CityDTO granada = new CityDTO();
        granada.setId(2L);
        granada.setName("Granada");

        DestinationDTO granadaDestination = new DestinationDTO();
        granadaDestination.setId(100L);
        granadaDestination.setCity(granada);
        granadaDestination.setJourneyTimeInMinutes(60L);

        CityDTO madrid = new CityDTO();
        madrid.setId(3L);
        madrid.setName("Madrid");

        DestinationDTO madridDestination = new DestinationDTO();
        madridDestination.setId(101L);
        madridDestination.setCity(madrid);
        madridDestination.setJourneyTimeInMinutes(90L);

        CityDTO zaragoza = new CityDTO();
        zaragoza.setId(1L);
        zaragoza.setName("Zaragoza");
        zaragoza.setDestinations(Arrays.asList(granadaDestination, madridDestination));

        wireMockRule.stubFor(
            get(WireMock.urlEqualTo("/v1/city/1"))
                .willReturn(
                    aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(JsonUtil.toJson(zaragoza))));
    }

    @Test
    public void test_obtain_city_with_destinations_by_id() {
        // GIVEN the zaragoza id
        Long zaragozaId = 1L;

        // WHEN it is retrieved from the remote server
        City zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);

        /// THEN a city model with Zaragoza's data is returned
        assertThat(zaragoza, notNullValue());
        assertThat(zaragoza.getId(), equalTo(1L));
        assertThat(zaragoza.getName(), equalTo("Zaragoza"));
        // AND it has two destinations
        assertThat(zaragoza.getDestinations().size(), equalTo(2));
        // AND the first destination is Granada
        Destination destination = zaragoza.getDestinations().get(0);
        assertThat(destination.getId(), equalTo(100L));
        assertThat(destination.getCity().getId(), equalTo(2L));
        assertThat(destination.getCity().getName(), equalTo("Granada"));
        assertThat(destination.getJourneyTime(), equalTo(Duration.ofMinutes(60)));
        // AND the second destination is Madrid
        destination = zaragoza.getDestinations().get(1);
        assertThat(destination.getId(), equalTo(101L));
        assertThat(destination.getCity().getId(), equalTo(3L));
        assertThat(destination.getCity().getName(), equalTo("Madrid"));
        assertThat(destination.getJourneyTime(), equalTo(Duration.ofMinutes(90)));
    }

    @Test
    public void test_cache() {
        // GIVEN the zaragoza id
        Long zaragozaId = 1L;

        // WHEN it is retrieved tree times
        City zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);
        zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);
        zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);

        /// THEN a city model with Zaragoza's data is returned
        assertThat(zaragoza, notNullValue());
        assertThat(zaragoza.getId(), equalTo(1L));
        assertThat(zaragoza.getName(), equalTo("Zaragoza"));
        // AND it has two destinations
        assertThat(zaragoza.getDestinations().size(), equalTo(2));
        // AND the first destination is Granada
        Destination destination = zaragoza.getDestinations().get(0);
        assertThat(destination.getId(), equalTo(100L));
        assertThat(destination.getCity().getId(), equalTo(2L));
        assertThat(destination.getCity().getName(), equalTo("Granada"));
        assertThat(destination.getJourneyTime(), equalTo(Duration.ofMinutes(60)));
        // AND the second destination is Madrid
        destination = zaragoza.getDestinations().get(1);
        assertThat(destination.getId(), equalTo(101L));
        assertThat(destination.getCity().getId(), equalTo(3L));
        assertThat(destination.getCity().getName(), equalTo("Madrid"));
        assertThat(destination.getJourneyTime(), equalTo(Duration.ofMinutes(90)));

        // AND the remote server is called only once
        WireMock.verify(1, getRequestedFor(WireMock.urlEqualTo("/v1/city/1")));
    }

    @Test
    public void test_cache_expires() throws InterruptedException {
        // GIVEN the zaragoza id
        Long zaragozaId = 1L;
        City zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);

        // WHEN 2 seconds passes and the service is called again
        Thread.sleep(1000);
        zaragoza = cityGateway.obtainWithDestinationsById(zaragozaId);

        // AND the remote server is called two times
        WireMock.verify(2, getRequestedFor(WireMock.urlEqualTo("/v1/city/1")));
    }
}
