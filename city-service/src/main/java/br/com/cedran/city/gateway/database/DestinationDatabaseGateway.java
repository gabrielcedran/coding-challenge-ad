package br.com.cedran.city.gateway.database;

import br.com.cedran.city.gateway.DestinationGateway;
import br.com.cedran.city.gateway.database.assembler.DestinationAssembler;
import br.com.cedran.city.gateway.database.mysql.DestinationDatabaseRepository;
import br.com.cedran.city.gateway.database.mysql.entity.DestinationEntity;
import br.com.cedran.city.model.Destination;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DestinationDatabaseGateway implements DestinationGateway {

    private DestinationDatabaseRepository destinationDatabaseRepository;

    @Override
    public Destination obtainByOriginAndDestination(Long originCity, Long destinationCity) {
        return destinationDatabaseRepository.findByOriginIdAndDestinationId(originCity, destinationCity).map(DestinationAssembler::fromDestinationEntity).orElse(null);
    }

    @Override
    public Destination create(Destination destination) {
        DestinationEntity destinationEntity = destinationDatabaseRepository.save(DestinationAssembler.fromDestination(destination));
        return DestinationAssembler.fromDestinationEntity(destinationEntity);
    }

    @Override
    public void delete(Destination destination) {
        destinationDatabaseRepository.deleteById(destination.getId());
    }

    @Override
    public Destination obtainById(Long id) {
        return destinationDatabaseRepository.findById(id).map(DestinationAssembler::fromDestinationEntity).orElse(null);
    }
}
