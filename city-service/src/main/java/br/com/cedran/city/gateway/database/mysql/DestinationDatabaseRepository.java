package br.com.cedran.city.gateway.database.mysql;

import br.com.cedran.city.gateway.database.mysql.entity.DestinationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinationDatabaseRepository extends CrudRepository<DestinationEntity, Long> {

    Optional<DestinationEntity> findByOriginIdAndDestinationId(Long originId, Long destinationId);

}
