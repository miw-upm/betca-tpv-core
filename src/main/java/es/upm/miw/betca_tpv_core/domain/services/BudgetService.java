package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.persistence.BudgetPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class BudgetService implements IBudgetService {

    private final BudgetPersistence budgetPersistence;

    @Autowired
    public BudgetService(BudgetPersistence budgetPersistence) {
        this.budgetPersistence = budgetPersistence;
    }

    public Mono<Budget> create(Budget budget) {
        budget.setId(null);
        budget.setReference(UUIDBase64.URL.encode());
        budget.setCreationDate(LocalDateTime.now());
        return this.budgetPersistence.create(budget);
    }

    public Mono<Budget> read(String id) {
        return this.budgetPersistence.readById(id);
    }
}
