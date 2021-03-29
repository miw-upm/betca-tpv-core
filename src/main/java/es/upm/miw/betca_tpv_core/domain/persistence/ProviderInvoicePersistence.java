package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProviderInvoicePersistence {

    Flux< ProviderInvoice > findAll();

    Mono< ProviderInvoice > create(ProviderInvoice providerInvoice);

    Mono< ProviderInvoice > readByNumber(Integer number);

    Mono< ProviderInvoice > update(Integer number, ProviderInvoice dataProviderInvoice);

    Mono< Void > delete(Integer number);

}
