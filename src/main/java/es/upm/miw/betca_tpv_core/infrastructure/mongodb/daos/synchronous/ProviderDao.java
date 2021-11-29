package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderDao extends MongoRepository< ProviderEntity, String > {
}
