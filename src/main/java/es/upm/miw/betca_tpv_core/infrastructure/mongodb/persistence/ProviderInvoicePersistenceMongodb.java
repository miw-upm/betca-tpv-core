package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderInvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderInvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderInvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ProviderInvoicePersistenceMongodb implements ProviderInvoicePersistence {

    private ProviderInvoiceReactive providerInvoiceReactive;

    @Autowired
    public ProviderInvoicePersistenceMongodb(ProviderInvoiceReactive providerInvoiceReactive) {
        this.providerInvoiceReactive = providerInvoiceReactive;
    }

    @Override
    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoiceReactive.findAll()
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }
}
