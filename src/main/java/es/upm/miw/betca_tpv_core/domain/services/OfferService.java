package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfOfferBuilder;
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
        return this.offerPersistence.create(offer);
    }

    public Mono<Offer> read(String reference) {
        return this.offerPersistence.readByReference(reference);
    }

    public Mono<Offer> update(String reference, Offer updatedOffer) {
        return this.offerPersistence.update(reference, updatedOffer);
    }

    public Mono<byte[]> print(String reference) {
        Mono<Offer> offer = this.offerPersistence.readByReference(reference);
        return offer.map(new PdfOfferBuilder()::generateOffer);
    }
}