package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderInvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderInvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderInvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProviderInvoicePersistenceMongodb implements ProviderInvoicePersistence {

    private ProviderInvoiceReactive providerInvoiceReactive;
    private ProviderReactive providerReactive;

    @Autowired
    public ProviderInvoicePersistenceMongodb(ProviderInvoiceReactive providerInvoiceReactive, ProviderReactive providerReactive) {
        this.providerInvoiceReactive = providerInvoiceReactive;
        this.providerReactive = providerReactive;
    }

    @Override
    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoiceReactive.findAll()
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }

    @Override
    public Mono< ProviderInvoice > create(ProviderInvoice providerInvoice) {
        return this.assertNumberNotExist(providerInvoice.getNumber())
                .then(Mono.just(providerInvoice.getProviderCompany()))
                .flatMap(providerCompany -> this.providerReactive.findByCompany(providerCompany)
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent company: " + providerCompany)
                        )))
                .map(providerEntity -> new ProviderInvoiceEntity(providerInvoice, providerEntity))
                .flatMap(this.providerInvoiceReactive::save)
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }

    private Mono< Void > assertNumberNotExist(Integer number) {
        return this.providerInvoiceReactive.findByNumber(number)
                .flatMap(providerInvoiceEntity -> Mono.error(
                        new ConflictException("Provider Invoice number already exists: " + number)
                ));
    }
}
