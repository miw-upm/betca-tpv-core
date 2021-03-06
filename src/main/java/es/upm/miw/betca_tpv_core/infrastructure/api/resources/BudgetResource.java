package es.upm.miw.betca_tpv_core.infrastructure.api.resources;
import es.upm.miw.betca_tpv_core.domain.model.Budget;
import es.upm.miw.betca_tpv_core.domain.services.BudgetService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.BudgetDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.BudgetReferenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@Rest
@RequestMapping(BudgetResource.BUDGETS)
public class BudgetResource {
    public static final String BUDGETS = "/budgets";
    public static final String RECEIPT = "/receipt";
    public static final String ID_ID = "/{id}";
    public static final String ID = "/id";
    private final BudgetService budgetService;

    @Autowired
    public BudgetResource(BudgetService budgetService) {

        this.budgetService = budgetService;

    }
    @PostMapping(produces = {"application/json"})
    public Mono<Budget> create(@Valid @RequestBody Budget budget) {
        return this.budgetService.create(budget);
    }
    @GetMapping(value = ID_ID +RECEIPT, produces = {"application/pdf", "application/json"})
    public Mono< byte[] > read(@PathVariable String id) {
        return this.budgetService.read(id);
    }
    @GetMapping(ID_ID)
    public Mono<BudgetDto> findById(@PathVariable String id) {
        return this.budgetService.findById(id)
                .map(BudgetDto::new);
    }
    @GetMapping(ID)
    public Mono<BudgetReferenceDto> findNullSafe(@RequestParam(required = false) String id) {
        return this.budgetService.findNullSafe(id)
                .collectList()
                .map(BudgetReferenceDto::new);
    }



}
