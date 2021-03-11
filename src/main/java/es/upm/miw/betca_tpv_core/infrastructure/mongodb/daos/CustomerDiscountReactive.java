package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.CustomerDiscountEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface CustomerDiscountReactive extends ReactiveSortingRepository< CustomerDiscountEntity, String > {
    Flux< CustomerDiscountEntity > findByUserPhone(String userPhone);
}
