package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import es.upm.miw.betca_tpv_core.domain.services.CreditSaleService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(CreditSaleResource.CREDIT_SALE)
public class CreditSaleResource {
    public static final String CREDIT_SALE = "/credit-sale";

    public static final String SEARCH = "/search";

    private CreditSaleService creditSaleService;

    @Autowired
    public CreditSaleResource(CreditSaleService creditSaleService) {
        this.creditSaleService = creditSaleService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<CreditSale> create(@Valid @RequestBody CreditSale creditSale) {
        creditSale.doDefault();
        return this.creditSaleService.create(creditSale);
    }

    @GetMapping(SEARCH)
    public Flux< CreditSale > findByUnfinished(@RequestParam(required = false) Boolean payed) {
        return this.creditSaleService.findByPayed(payed);
    }
}
