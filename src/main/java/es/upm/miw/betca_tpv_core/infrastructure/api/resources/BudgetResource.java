package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.services.IBudgetService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(BudgetResource.BUDGETS)
public class BudgetResource {
    public static final String BUDGETS = "/budgets";
    public static final String BUDGET_ID = "/{id}";

    @Autowired
    IBudgetService budgetService;

    @PostMapping(produces = {"application/json"})
    public Mono<Budget> create(@Valid @RequestBody Budget budget) {
        return this.budgetService.create(budget);
    }

    @GetMapping(BUDGET_ID)
    public Mono<Budget> read(@PathVariable String id) {
        return this.budgetService.read(id);
    }

}
