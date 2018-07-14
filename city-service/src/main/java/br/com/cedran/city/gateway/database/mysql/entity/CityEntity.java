package br.com.cedran.city.gateway.database.mysql.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CITY")
@Getter
@Setter
public class CityEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "origin")
    private List<DestinationEntity> destinations;

}
