package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAuditEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockAuditDao extends MongoRepository<StockAuditEntity, String> {

}
