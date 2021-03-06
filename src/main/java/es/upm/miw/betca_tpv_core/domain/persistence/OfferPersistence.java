package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OfferPersistence {
    Flux<Offer> findByReferenceAndDescriptionNullSafe(String reference, String description);

    Mono<Offer> create(Offer offer);

    Mono<Offer> readByReference(String reference);

    Mono<Offer> update(String reference, Offer updatedOffer);

    Mono<Void> delete(String reference);
}
