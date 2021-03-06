package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditDao extends MongoRepository<CreditEntity, String > {
}
