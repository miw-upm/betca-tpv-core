package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.services.ProviderInvoiceService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

@Rest
@RequestMapping(ProviderInvoiceResource.PROVIDER_INVOICES)
public class ProviderInvoiceResource {
    public static final String PROVIDER_INVOICES = "/provider-invoices";

    private ProviderInvoiceService providerInvoiceService;

    @Autowired
    public ProviderInvoiceResource(ProviderInvoiceService providerInvoiceService) {
        this.providerInvoiceService = providerInvoiceService;
    }

    @GetMapping(produces = {"application/json"})
    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoiceService.findAll();
    }

}