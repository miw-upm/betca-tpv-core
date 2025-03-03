package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StockAuditReactive extends ReactiveMongoRepository<StockAuditEntity, String> {
}
