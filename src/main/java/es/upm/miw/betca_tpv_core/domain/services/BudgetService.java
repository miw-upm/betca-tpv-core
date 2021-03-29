package es.upm.miw.betca_tpv_core.domain.services;
import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.persistence.BudgetPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfBudgetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class BudgetService {
    private final BudgetPersistence budgetPersistence;

    @Autowired
    public BudgetService(BudgetPersistence budgetPersistence) {
        this.budgetPersistence = budgetPersistence;

    }

    public Mono<Budget> create(Budget budget) {
        budget.setId(null);
        budget.setCreationDate(LocalDateTime.now());
        return this.budgetPersistence.create(budget);
    }
    public Mono<byte[]> read(String id) {
        return this.budgetPersistence.read(id)
                .map(new PdfBudgetBuilder()::generateBudget);
    }
    public Mono<Budget> findById(String id) {
        return this.budgetPersistence.findById(id);
    }
    public Flux< String > findNullSafe(String id) {
        return this.budgetPersistence.findNullSafe(id);
    }
}
