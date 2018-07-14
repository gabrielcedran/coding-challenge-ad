package br.com.cedran.city.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Destination {

    private Long id;
    private City originCity;
    private City destinationCity;
    private Duration journeyTime;
}
