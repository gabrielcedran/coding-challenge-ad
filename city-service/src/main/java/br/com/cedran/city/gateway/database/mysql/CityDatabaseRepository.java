package br.com.cedran.city.gateway.database.mysql;

import br.com.cedran.city.gateway.database.mysql.entity.CityEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityDatabaseRepository extends CrudRepository<CityEntity, Long> {

    Optional<CityEntity> findByNameContainingIgnoreCase(String name);

    @Query("select c from CityEntity c left join fetch c.destinations d where c.id = :id")
    Optional<CityEntity> obtainByIdWithDestinations(@Param(value = "id") Long id);

}
