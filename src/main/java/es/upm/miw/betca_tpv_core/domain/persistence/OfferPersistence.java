package es.upm.miw.betca_tpv_core.domain.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OfferPersistence {

    Mono<Offer> create(Offer offer);

    Flux<Offer> findByReferenceAndDescriptionAndCreationDateAndExpiryDateAndDiscountNullSafe(String reference, String description);
}