package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.StockAlarmEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockAlarmDao extends MongoRepository<StockAlarmEntity, String> {
}
