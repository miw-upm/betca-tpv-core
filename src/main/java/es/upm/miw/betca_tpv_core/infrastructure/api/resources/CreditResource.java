package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.services.CreditService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(CreditResource.CREDIT)
public class CreditResource {

    public static final String CREDIT = "/credit";

    public static final String SEARCH = "/search";

    private CreditService creditService;

    @Autowired
    public CreditResource(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Credit> create(@Valid @RequestBody Credit credit) {
        credit.doDefault();
        return this.creditService.create(credit);
    }

    @GetMapping(SEARCH)
    public Mono< Credit > findByUserReference(@RequestParam(required = true) String userReference) {
        return this.creditService.findByUserReference(userReference);
    }
}
