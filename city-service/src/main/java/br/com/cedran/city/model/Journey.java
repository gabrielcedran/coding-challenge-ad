package br.com.cedran.city.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public class Journey {

    private Long id;
    private City origin;
    private List<City> connections;
    private City destination;
    private Duration journeyTime;
}
