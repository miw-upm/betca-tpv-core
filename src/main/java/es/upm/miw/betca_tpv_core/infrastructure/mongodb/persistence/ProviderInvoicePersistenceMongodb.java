package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderInvoicePersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderInvoiceReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ProviderReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderInvoiceEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public class ProviderInvoicePersistenceMongodb implements ProviderInvoicePersistence {

    private static final String NON_EXISTING_PROVIDER_INVOICE_WITH_NUMBER = "Non existing provider invoice with number: ";
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

    @Override
    public Mono< ProviderInvoice > readByNumber(Integer number) {
        return this.providerInvoiceReactive.findByNumber(number)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(NON_EXISTING_PROVIDER_INVOICE_WITH_NUMBER + number)))
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }

    @Override
    public Mono< ProviderInvoice > update(Integer number, ProviderInvoice providerInvoice) {
        Mono< ProviderInvoiceEntity > providerInvoiceEntityMono;
        if (!number.equals(providerInvoice.getNumber())) {
            providerInvoiceEntityMono = this.assertNumberNotExist(providerInvoice.getNumber())
                .then(this.providerInvoiceReactive.findByNumber(number));
        } else {
            providerInvoiceEntityMono = this.providerInvoiceReactive.findByNumber(number);
        }
        return providerInvoiceEntityMono
                .switchIfEmpty(Mono
                        .error(new NotFoundException(NON_EXISTING_PROVIDER_INVOICE_WITH_NUMBER + number)))
                .flatMap(providerInvoiceEntity -> {
                    BeanUtils.copyProperties(providerInvoice, providerInvoiceEntity);
                    return this.providerReactive.findByCompany(providerInvoice.getProviderCompany())
                            .switchIfEmpty(Mono.error(
                                    new NotFoundException("Non existent company: " + providerInvoice.getProviderCompany())
                            ))
                            .map(providerEntity -> {
                                providerInvoiceEntity.setProviderEntity(providerEntity);
                                return providerInvoiceEntity;
                            });
                })
                .flatMap(this.providerInvoiceReactive::save)
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }

    @Override
    public Mono< Void > delete(Integer number) {
        Mono<String> idMono = this.providerInvoiceReactive.findByNumber(number)
                .switchIfEmpty(Mono.error(new NotFoundException(NON_EXISTING_PROVIDER_INVOICE_WITH_NUMBER + number)))
                .map(ProviderInvoiceEntity::getId);
        return this.providerInvoiceReactive.deleteById(idMono);
    }

    @Override
    public Flux< ProviderInvoice > findByCreationDateBetweenInclusive(LocalDate startDate, LocalDate endDate) {
        return this.providerInvoiceReactive.findByCreationDateBetweenInclusive(startDate, endDate)
                .map(ProviderInvoiceEntity::toProviderInvoice);
    }

}
