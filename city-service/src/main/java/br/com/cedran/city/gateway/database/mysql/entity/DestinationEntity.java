package br.com.cedran.city.gateway.database.mysql.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "DESTINATION")
@Getter
@Setter
public class DestinationEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ORIGIN_CITY")
    private CityEntity origin;

    @ManyToOne
    @JoinColumn(name = "ID_DESTINATION_CITY")
    private CityEntity destination;

    @Column(name = "JOURNEY_TIME_IN_MINUTES")
    private Long journeyTimeInMinutes;

}
