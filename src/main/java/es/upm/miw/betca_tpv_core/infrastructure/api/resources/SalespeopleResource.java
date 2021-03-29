package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.services.SalespeopleService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.SalespeopleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Rest
@RequestMapping(SalespeopleResource.SALESPEOPLE)
public class SalespeopleResource {
    public static final String SALESPEOPLE="/salespeople";
    public static final String SEARCH_SALESPEOPLE="/search";
    public static final String SEARCH_SECOND="/search_second";

    private SalespeopleService salespeopleService;

    @Autowired
    public SalespeopleResource(SalespeopleService salespeopleService) {
        this.salespeopleService = salespeopleService;
    }

    @GetMapping(SEARCH_SALESPEOPLE)
    public Flux<Salespeople> findBySalespersonAndSalesDate(@RequestParam(required = false) String salesperson, @RequestParam(required = false)LocalDate localDate){
        return this.salespeopleService.findBySalespersonAndSalesDate(salesperson,localDate)
                .map(Salespeople::ofSalespeopleSalesDateFinalValue);
    }

    @GetMapping(SEARCH_SECOND)
    public Flux<SalespeopleDto> findBySalesDate(@RequestParam(required = false) LocalDate localDate){
        return this.salespeopleService.findBySalesDate(localDate)
                .map(SalespeopleDto::new);
    }
}
