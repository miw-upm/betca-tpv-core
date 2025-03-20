package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.services.IBudgetService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(BudgetResource.BUDGETS)
public class BudgetResource {
    public static final String BUDGETS = "/budgets";
    public static final String BUDGET_ID = "/{id}";
    public static final String BUDGET_SEARCH = "/search";
    public static final String BUDGET_SEARCH_BY_REFERENCE = "/search-by-reference";

    @Autowired
    IBudgetService budgetService;

    @PostMapping(produces = {"application/json"})
    public Mono<Budget> create(@Valid @RequestBody Budget budget) {
        return this.budgetService.create(budget);
    }

    @GetMapping(BUDGET_ID)
    public Mono<ResponseEntity<Budget>> read(@PathVariable String id) {
        return this.budgetService.read(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(BUDGET_ID)
    public Mono<Void> delete(@PathVariable String id) {
        return this.budgetService.delete(id);
    }

    @PutMapping(BUDGET_ID)
    public Mono<Budget> update(@PathVariable String id, @Valid @RequestBody Budget budget) {
        return this.budgetService.update(id, budget);
    }

    @GetMapping(BUDGET_SEARCH)
    public Flux<Budget> findByReferenceLikeNullSafe(@RequestParam(required = false) String reference) {
        return this.budgetService.findByReferenceLikeNullSafe(reference);
    }

    @GetMapping(BUDGET_SEARCH_BY_REFERENCE)
    public Flux<Budget> findByReferenceLike(@RequestParam String reference) {
        return this.budgetService.findByReferenceLike(reference);
    }

}
