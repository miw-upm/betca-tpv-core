package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CreditSaleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditSaleDao extends MongoRepository<CreditSaleEntity, String > {
}
