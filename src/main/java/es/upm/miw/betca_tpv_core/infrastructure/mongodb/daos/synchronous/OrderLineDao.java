package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderLineEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderLineDao extends MongoRepository<OrderLineEntity, String > {
}
