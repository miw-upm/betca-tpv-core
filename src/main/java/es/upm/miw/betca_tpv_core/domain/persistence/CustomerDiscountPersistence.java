package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerDiscountPersistence {
    Flux<CustomerDiscount> findByUser(String user);
    Flux<CustomerDiscount> findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(
            String note, Double discount, Double minimumPurchase, String user);
    Mono< CustomerDiscount > create (CustomerDiscount customerDiscount);
    Mono<CustomerDiscount> update (String id, CustomerDiscount customerDiscount);
    Mono<CustomerDiscount> readById(String id);
    Mono<Void> delete(String id);
}
