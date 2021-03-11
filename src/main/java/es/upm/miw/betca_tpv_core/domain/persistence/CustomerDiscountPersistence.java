package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.CustomerDiscount;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerDiscountPersistence {

    Flux< CustomerDiscount > findByUserPhone(String userPhone);
}
