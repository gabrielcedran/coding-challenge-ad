package br.com.cedran.city.gateway.database;

import br.com.cedran.city.gateway.CityGateway;
import br.com.cedran.city.model.City;
import br.com.cedran.city.model.Destination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@ComponentScan(basePackages = {"br.com.cedran.city.gateway.database", "br.com.cedran.city.gateway.database.assembler"})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:sql/city_gateway_test.sql"})
public class CityDatabaseGatewayTest {

    @Autowired
    private CityGateway cityGateway;

    @Test
    public void obtain_by_id_with_destinations_test() {
        // GIVEN the city id of Zaragoza
        Long cityId = 1L;

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByIdWithDestinations(cityId);

        // THEN the city id is 1
        assertThat(city.getId(), equalTo(cityId));
        // AND the city name is 'Zaragoza'
        assertThat(city.getName(), equalTo("Zaragoza"));
        // AND there are two destinations
        assertThat(city.getDestinations().size(), equalTo(2));

        // AND the first destination is Granada
        Destination destination1 = city.getDestinations().get(0);
        assertThat(destination1.getDestinationCity().getId(), equalTo(2L));
        assertThat(destination1.getDestinationCity().getName(), equalTo("Granada"));
        assertThat(destination1.getJourneyTime().toMinutes(), equalTo(60L));
        // AND Granada does not have the destinations loaded
        assertThat(destination1.getDestinationCity().getDestinations(), nullValue());


        // AND the first destination is Madrid
        Destination destination2 = city.getDestinations().get(1);
        assertThat(destination2.getDestinationCity().getId(), equalTo(3L));
        assertThat(destination2.getDestinationCity().getName(), equalTo("Madrid"));
        assertThat(destination2.getJourneyTime().toMinutes(), equalTo(90L));
        // AND Madrid does not have the destinations loaded
        assertThat(destination2.getDestinationCity().getDestinations(), nullValue());
    }

    @Test
    public void try_to_obtain_city_without_destinations() {
        // GIVEN the city id of Madrid
        Long cityId = 3L;

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByIdWithDestinations(cityId);

        // THEN the city id is 3
        assertThat(city.getId(), equalTo(cityId));
        // AND the city name is 'Madrid'
        assertThat(city.getName(), equalTo("Madrid"));
        // AND there are not destinarions
        assertThat(city.getDestinations(), empty());
    }

    @Test
    public void try_to_obtain_non_existent_city_with_destinations() {
        // GIVEN a non existent city id
        Long cityId = -1000L;

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByIdWithDestinations(cityId);

        // THEN a null value is returned
        assertThat(city, nullValue());
    }

    @Test
    public void obtain_by_name_matching_case() {
        // GIVEN the city name Zaragoza
        String cityName = "Zaragoza";

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByName(cityName);

        // THEN the city is returned without destinations
        assertThat(city.getId(), equalTo(1L));
        assertThat(city.getName(), equalTo("Zaragoza"));
        assertThat(city.getDestinations(), nullValue());
    }

    @Test
    public void obtain_by_name_not_matching_case() {
        // GIVEN the city name zaraGozA
        String cityName = "zaraGozA";

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByName(cityName);

        // THEN the city is returned without destinations
        assertThat(city.getId(), equalTo(1L));
        assertThat(city.getName(), equalTo("Zaragoza"));
        assertThat(city.getDestinations(), nullValue());
    }

    @Test
    public void obtain_by_non_existent_name() {
        // GIVEN the city name zaraGozA
        String cityName = "ZZZZZZ";

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainByName(cityName);

        // THEN a null value is returned
        assertThat(city, nullValue());
    }

    @Test
    public void obtain_by_id() {
        // GIVEN the city name zaraGozA
        Long cityId = 1L;

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainById(cityId);

        // THEN Zaragoza is returned without its destinations
        assertThat(city.getId(), equalTo(1L));
        assertThat(city.getName(), equalTo("Zaragoza"));
        assertThat(city.getDestinations(), nullValue());
    }


    @Test
    public void obtain_by_non_existent_id() {
        // GIVEN the city name zaraGozA
        Long cityId = 10000L;

        // WHEN the city with its destinations are loaded
        City city = cityGateway.obtainById(cityId);

        // THEN a null value is returned
        assertThat(city, nullValue());
    }

    @Test
    public void create_new_city() {
        // GIVEN a non existent city without id
        City city = new City(null, "Galicia", null);

        // WHEN the city with its destinations are loaded
        city = cityGateway.create(city);

        // THEN a new city is returned with id and the provided name
        assertThat(city.getId(), notNullValue());
        assertThat(city.getName(), equalTo("Galicia"));
        // AND it is possible to find it by id
        assertThat(cityGateway.obtainById(city.getId()), notNullValue());

    }

    @Test
    public void update_city() {
        // GIVEN a non existent city without id
        City city = new City(3L, "Valencia", null);

        // WHEN the city with its destinations are loaded
        city = cityGateway.update(city);

        // THEN a new city is returned with id and the provided name
        assertThat(city.getId(), equalTo(3L));
        assertThat(city.getName(), equalTo("Valencia"));
        // AND if it is searched by id, the name is also updated
        assertThat(cityGateway.obtainById(city.getId()).getName(), equalTo("Valencia"));

    }
}
