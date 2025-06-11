package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerDiscountDao extends MongoRepository<CustomerDiscountEntity, String> {
}
