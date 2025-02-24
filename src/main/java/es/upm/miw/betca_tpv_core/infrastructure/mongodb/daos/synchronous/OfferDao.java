package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OfferEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfferDao extends MongoRepository<OfferEntity, String> {
}
