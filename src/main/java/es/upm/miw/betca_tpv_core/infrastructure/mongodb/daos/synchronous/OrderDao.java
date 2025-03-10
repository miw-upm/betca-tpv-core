package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderDao extends MongoRepository<OrderEntity, String> {
}
