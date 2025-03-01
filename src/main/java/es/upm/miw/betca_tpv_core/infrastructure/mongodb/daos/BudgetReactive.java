package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.BudgetEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BudgetReactive extends ReactiveMongoRepository<BudgetEntity, String> {}
