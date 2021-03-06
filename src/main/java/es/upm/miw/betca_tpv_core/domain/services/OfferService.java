package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OfferService {

    private OfferPersistence offerPersistence;

    @Autowired
    public OfferService(OfferPersistence offerPersistence) {
        this.offerPersistence = offerPersistence;
    }

    public Flux<Offer> findByReferenceAndDescriptionNullSafe(String reference, String description) {
        return this.offerPersistence.findByReferenceAndDescriptionNullSafe(reference, description);
    }

    public Mono<Offer> create(Offer offer) {
        //offer.setReference(UUIDBase64.URL.encode());
        //offer.setCreationDate(LocalDateTime.now());
        return this.offerPersistence.create(offer);
    }
}

