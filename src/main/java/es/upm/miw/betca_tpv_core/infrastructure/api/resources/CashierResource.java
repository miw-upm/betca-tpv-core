package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.CashierClose;
import es.upm.miw.betca_tpv_core.domain.model.CashierState;
import es.upm.miw.betca_tpv_core.domain.services.CashierService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashierLastDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(CashierResource.CASHIERS)
public class CashierResource {
    public static final String CASHIERS = "/cashiers";

    public static final String LAST = "/last";
    public static final String STATE = "/state";

    private final CashierService cashierService;

    @Autowired
    public CashierResource(CashierService cashierService) {
        this.cashierService = cashierService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono< Void > createOpened() {
        return this.cashierService.createOpened();
    }

    @GetMapping(value = LAST)
    public Mono< CashierLastDto > findLast() {
        return this.cashierService.findLast()
                .map(CashierLastDto::new);
    }

    @GetMapping(value = LAST + STATE)
    public Mono< CashierState > findLastState() {
        return this.cashierService.findLastState();
    }

    @PatchMapping(value = LAST)
    public Mono< Cashier > closeCashier(@Valid @RequestBody CashierClose cashierClose) {
        return cashierService.close(cashierClose);
    }
}
