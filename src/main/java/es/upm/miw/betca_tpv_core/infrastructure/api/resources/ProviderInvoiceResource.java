package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.services.ProviderInvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(ProviderInvoiceResource.PROVIDER_INVOICES)
public class ProviderInvoiceResource {
    public static final String PROVIDER_INVOICES = "/provider-invoices";
    public static final String NUMBER = "/{number}";

    private ProviderInvoiceService providerInvoiceService;

    @Autowired
    public ProviderInvoiceResource(ProviderInvoiceService providerInvoiceService) {
        this.providerInvoiceService = providerInvoiceService;
    }

    @GetMapping
    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoiceService.findAll();
    }

    @PostMapping(produces = {"application/json"})
    public Mono< ProviderInvoice > create(@Valid @RequestBody ProviderInvoice providerInvoice) {
        return this.providerInvoiceService.create(providerInvoice);
    }

    @GetMapping(NUMBER)
    public Mono< ProviderInvoice > read(@PathVariable Integer number) {
        return this.providerInvoiceService.findByNumber(number);
    }

}
