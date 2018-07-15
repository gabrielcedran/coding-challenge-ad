package br.com.cedran.route.model;

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
    private Long cityId;
    private String name;
    private Duration journeyTime;

}
