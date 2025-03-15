package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.BudgetEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetDao extends MongoRepository<BudgetEntity, String> {
}
