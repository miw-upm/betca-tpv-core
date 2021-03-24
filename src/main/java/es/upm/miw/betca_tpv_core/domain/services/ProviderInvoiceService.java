package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderInvoicePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProviderInvoiceService {

    private ProviderInvoicePersistence providerInvoicePersistence;

    @Autowired
    public ProviderInvoiceService(ProviderInvoicePersistence providerInvoicePersistence) {
        this.providerInvoicePersistence = providerInvoicePersistence;
    }

    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoicePersistence.findAll();
    }

    public Mono< ProviderInvoice > create(ProviderInvoice providerInvoice) {
        return this.providerInvoicePersistence.create(providerInvoice);
    }
}
