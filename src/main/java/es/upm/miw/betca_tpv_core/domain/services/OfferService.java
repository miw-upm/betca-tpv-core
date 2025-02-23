package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OfferService {

    private final OfferPersistence offerPersistence;

    @Autowired
    public OfferService(OfferPersistence offerPersistence) {
        this.offerPersistence = offerPersistence;
    }

    public Mono<Offer> create(Offer offer) {
        offer.setCreationDate(LocalDateTime.now());
        return this.offerPersistence.create(offer);
    }

    public Flux<Offer> findByReferenceAndDescriptionAndCreationDateAndExpiryDateAndDiscountNullSafe(String reference, String description) {
        return this.offerPersistence.findByReferenceAndDescriptionAndCreationDateAndExpiryDateAndDiscountNullSafe(reference, description);
    }
}
