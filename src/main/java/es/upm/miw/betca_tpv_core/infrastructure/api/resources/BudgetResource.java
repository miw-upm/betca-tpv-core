package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.services.IBudgetService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(BudgetResource.BUDGETS)
public class BudgetResource {
    public static final String BUDGETS = "/budgets";

    @Autowired
    IBudgetService budgetService;

    @PostMapping(produces = {"application/json"})
    public Mono<Budget> create(@Valid @RequestBody Budget budget) {
        return this.budgetService.create(budget);
    }

}
