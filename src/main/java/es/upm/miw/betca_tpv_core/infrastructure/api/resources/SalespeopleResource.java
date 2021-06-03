package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.services.SalespeopleService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SalespeopleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Rest
@RequestMapping(SalespeopleResource.SALESPEOPLE)
public class SalespeopleResource {
    public static final String SALESPEOPLE = "/salespeople";
    public static final String SEARCH_SALESPEOPLE = "/search";
    public static final String SEARCH_Month = "/search_month";

    private SalespeopleService salespeopleService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public SalespeopleResource(SalespeopleService salespeopleService) {
        this.salespeopleService = salespeopleService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Salespeople> create(@Valid @RequestBody Salespeople salespeople) {
        return this.salespeopleService.creat(salespeople);
    }

    @GetMapping(SEARCH_SALESPEOPLE)
    public Flux<Salespeople> findByUserMobileAndSalesDateBetween(@RequestParam(required = false) String userMobile,
                                                           @RequestParam(required = false) String dateBeginString,
                                                           @RequestParam(required = false) String dateEndString) {
        LocalDate dateBegin = LocalDate.parse(dateBeginString, formatter);
        LocalDate dateEnd = LocalDate.parse(dateEndString, formatter);
        return this.salespeopleService.findBySalespersonAndSalesDateBetween(userMobile, dateBegin, dateEnd)
                .map(Salespeople::ofSalespeopleSalesDateFinalValue);
    }

    @GetMapping(SEARCH_Month)
    public Flux<SalespeopleDto> findBySalesDate(@RequestParam(required = false) String dateBeginString,
                                                @RequestParam(required = false) String dateEndString) {
        LocalDate dateBegin = LocalDate.parse(dateBeginString, formatter);
        LocalDate dateEnd = LocalDate.parse(dateEndString, formatter);
        return this.salespeopleService.findBySalesDateBetween(dateBegin, dateEnd)
                .map(SalespeopleDto::new);
    }
}
