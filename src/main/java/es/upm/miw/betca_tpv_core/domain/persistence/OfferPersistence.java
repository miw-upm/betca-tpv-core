package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OfferPersistence {
    Flux<Offer> findByReferenceAndDescriptionNullSafe(String reference, String description);
}
