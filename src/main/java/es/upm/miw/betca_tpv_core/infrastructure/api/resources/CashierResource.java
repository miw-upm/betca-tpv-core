package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.services.CashierService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.CashierLastDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Rest
@RequestMapping(CashierResource.CASHIERS)
public class CashierResource {
    public static final String CASHIERS = "/cashiers";

    public static final String LAST = "/last";
    public static final String STATE = "/state";
    public static final String SEARCH = "/search";
    public static final String TOTALS = "/totals";
    public static final String UPDATE = "/update";
    public static final String MOVEMENT_IN = "/movement-in";
    public static final String MOVEMENT_OUT = "/movement-out";

    private CashierService cashierService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    @GetMapping(SEARCH)
    public Flux<Cashier> findCashierEntitiesByClosureDateBetween(
            @RequestParam(required = false) String dateBeginString,
            @RequestParam(required = false) String dateEndString) {
        if(dateBeginString == null || dateEndString == null){
            return this.cashierService.findAll();
        }
        LocalDateTime dateBegin = LocalDateTime.parse(dateBeginString, formatter);
        LocalDateTime dateEnd = LocalDateTime.parse(dateEndString, formatter);
        return this.cashierService.findCashierEntitiesByClosureDateBetween(dateBegin, dateEnd);
    }

    @GetMapping(value = SEARCH + TOTALS)
    public Mono<Cashier> findCashierEntitiesByClosureDateBetweenTotals(
            @RequestParam(required = false) String dateBeginString,
            @RequestParam(required = false) String dateEndString) {
        if(dateBeginString == null || dateEndString == null){
            return this.cashierService.findAllTotals();
        }
        LocalDateTime dateBegin = LocalDateTime.parse(dateBeginString, formatter);
        LocalDateTime dateEnd = LocalDateTime.parse(dateEndString, formatter);
        return this.cashierService.findCashierEntitiesByClosureDateBetweenTotals(dateBegin, dateEnd);
    }

    @GetMapping(value = LAST + STATE)
    public Mono< CashierState > findLastState() {
        return this.cashierService.findLastState();
    }

    @PatchMapping(value = LAST)
    public Mono<Cashier> closeCashier(@Valid @RequestBody CashierClose cashierClose) {
        return cashierService.close(cashierClose);
    }

    @PatchMapping(value = LAST + MOVEMENT_IN)
    public Mono< Cashier > movementCashierIn(@Valid @RequestBody CashierMovement cashierMovement) {
        return cashierService.movementIn(cashierMovement);
    }

    @PatchMapping(value = LAST + MOVEMENT_OUT)
    public Mono< Cashier > movementCashierOut(@Valid @RequestBody CashierMovement cashierMovement) {
        return cashierService.movementOut(cashierMovement);
    }
}
