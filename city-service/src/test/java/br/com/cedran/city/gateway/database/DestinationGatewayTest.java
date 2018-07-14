package br.com.cedran.city.gateway.database;

import br.com.cedran.city.gateway.DestinationGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@ComponentScan(basePackages = {"br.com.cedran.city.gateway.database", "br.com.cedran.city.gateway.assembler"})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:sql/city_gateway_test.sql"})
public class DestinationGatewayTest {

    @Autowired
    private DestinationGateway destinationGateway;

    @Test
    public void test_obtain_by_origin_and_destination() {
        // GIVEN the origin Zaragoza
        Long originId = 1L;
        // AND the destination Granada
        Long destinationId = 2L;

        // WHEN that route is searched
        Destination destination = destinationGateway.obtainByOriginAndDestination(originId, destinationId);

        // THEN a destinations is returned with id 1
        assertThat(destination, notNullValue());
        assertThat(destination.getId(), equalTo(1L));
        // AND the origin city is Zaragoza
        assertThat(destination.getOriginCity().getId(), equalTo(1L));
        assertThat(destination.getOriginCity().getName(), equalTo("Zaragoza"));
        // AND the destinations is not populated
        assertThat(destination.getOriginCity().getDestinations(), nullValue());
        // AND the destination city is Granada
        assertThat(destination.getDestinationCity().getId(), equalTo(2L));
        assertThat(destination.getDestinationCity().getName(), equalTo("Granada"));
        // AND the destinations is not populated
        assertThat(destination.getDestinationCity().getDestinations(), nullValue());
        // AND the journey time is 60 minutes
        assertThat(destination.getJourneyTime().toMinutes(), equalTo(60L));

    }

    @Test
    public void test_obtain_by_origin_and_destination_with_non_existent_origin() {
        // GIVEN a non existent origin
        Long originId = -1000L;
        // AND the destination Granada
        Long destinationId = 2L;

        // WHEN that route is searched
        Destination destination = destinationGateway.obtainByOriginAndDestination(originId, destinationId);

        // THEN a destinations is returned with id 1
        assertThat(destination, nullValue());

    }

    @Test
    public void test_obtain_by_origin_and_destination_with_non_existent_destination() {
        // GIVEN the origin Zaragoza
        Long originId = 1L;
        // AND a non existent destination
        Long destinationId = -1000L;

        // WHEN that route is searched
        Destination destination = destinationGateway.obtainByOriginAndDestination(originId, destinationId);

        // THEN a destinations is returned with id 1
        assertThat(destination, nullValue());

    }

    @Test
    public void test_create_destination() {
        // GIVEN the origin Madrid
        City madrid = new City(3L, "Madrid", null);
        // AND destination Zaragoza
        City zaragoza = new City(1L, "Zaragoza", null);
        // AND the time as 3 hours
        Duration journeyTime = Duration.ofHours(3);

        Destination destination = new Destination(null, madrid, zaragoza, journeyTime);

        // WHEN that route is searched
        destination = destinationGateway.create(destination);

        // THEN a destination is returned with populated is returned
        assertThat(destination.getId(), notNullValue());
        // AND the origin city is Madrid
        assertThat(destination.getOriginCity().getId(), equalTo(3L));
        assertThat(destination.getOriginCity().getName(), equalTo("Madrid"));
        // AND the origin city is Zaragoza
        assertThat(destination.getDestinationCity().getId(), equalTo(1L));
        assertThat(destination.getDestinationCity().getName(), equalTo("Zaragoza"));
        // AND the journey time is 3 hours
        assertThat(destination.getJourneyTime().toHours(), equalTo(3L));
        // AND that destination is found in the database
        assertThat(destinationGateway.obtainById(destination.getId()), notNullValue());

    }

    @Test
    public void test_delete_destination() {
        // GIVEN a destination stored in the database with id 1
        Destination destination = destinationGateway.obtainById(1L);

        // WHEN that route is searched
        destinationGateway.delete(destination);

        // THEN that destination is not found in the database anymore
        assertThat(destinationGateway.obtainById(1L), nullValue());

    }
}
