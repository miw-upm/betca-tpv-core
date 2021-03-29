package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoiceTotalTax;
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
    public static final String TOTAL_TAX_QUARTERS = "/total-tax/quarters";

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
        return this.providerInvoiceService.read(number);
    }

    @PutMapping(NUMBER)
    public Mono< ProviderInvoice > update(
            @PathVariable Integer number,
            @Valid @RequestBody ProviderInvoice providerInvoice
    ) {
        return this.providerInvoiceService.update(number, providerInvoice);
    }

    @DeleteMapping(NUMBER)
    public Mono< Void > delete(@PathVariable Integer number) {
        return this.providerInvoiceService.delete(number);
    }

    @GetMapping(TOTAL_TAX_QUARTERS + NUMBER)
    public Mono<ProviderInvoiceTotalTax> calculateTotalTaxByQuarter(@PathVariable(name = "number") Integer quarterNumber) {
        if (!isQuarterValid(quarterNumber)) {
            return Mono.error(new BadRequestException(
                    "Quarter number should be between 1 and 4. Given quarter number: " + quarterNumber
            ));
        }
        return this.providerInvoiceService.calculateTotalTaxByQuarter(quarterNumber);
    }

    private boolean isQuarterValid(Integer quarterNumber) {
        return quarterNumber >= 1 && quarterNumber <= 4;
    }

}
