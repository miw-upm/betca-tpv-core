package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfOfferBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OfferService {

    private final OfferPersistence offerPersistence;

    @Autowired
    public OfferService(OfferPersistence offerPersistence) {
        this.offerPersistence = offerPersistence;
    }

    public Mono<Offer> create(Offer offer) {
        return this.offerPersistence.create(offer);
    }

    public Flux<Offer> findByReferenceAndDescriptionAndDiscountNullSafe(String reference, String description, Integer discount) {
        return this.offerPersistence.findByReferenceAndDescriptionAndDiscountNullSafe(reference, description, discount);
    }

    public Mono<Offer> read(String reference) {
        return this.offerPersistence.readByReference(reference);
    }

    public Mono<Offer> update(String reference, Offer offer) {
        return this.offerPersistence.update(reference, offer);
    }

    public Mono<byte[]> readPdf(String reference) {
        return this.offerPersistence.readByReference(reference)
                .switchIfEmpty(Mono.error(new NotFoundException("Offer not found with reference: " + reference)))
                .map(new PdfOfferBuilder()::generateOffer);

    }
}
