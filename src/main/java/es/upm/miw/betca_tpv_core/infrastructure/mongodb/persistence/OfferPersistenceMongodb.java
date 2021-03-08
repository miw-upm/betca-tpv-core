package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OfferReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OfferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class OfferPersistenceMongodb implements OfferPersistence {

    private OfferReactive offerReactive;
    private ArticleReactive articleReactive;

    @Autowired
    public OfferPersistenceMongodb(OfferReactive offerReactive, ArticleReactive articleReactive) {
        this.offerReactive = offerReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Flux<Offer> findByReferenceAndDescriptionNullSafe(String reference, String description) {
        return this.offerReactive.findByReferenceAndDescriptionNullSafe(reference, description)
                .map(OfferEntity::toOffer);
    }

    @Override
    public Mono<Offer> create(Offer offer) {
        OfferEntity newOfferEnt = new OfferEntity(offer);
        // this.assertReferenceNotExist(offer.getReference())
        OfferEntity a = this.offerReactive.findByReference(offer.getReference()).block();
        if (a != null) {
            return Mono.error(new ConflictException("Offer Reference already exists : " + offer.getReference()));
        } else {
            return Flux.fromStream(offer.getArticleBarcodeList().stream())
                    .flatMap(barcode -> this.articleReactive.findByBarcode(barcode)
                            .switchIfEmpty(Mono.error(new NotFoundException("Article: " + barcode)))).doOnNext(newOfferEnt::add)
                    .then(this.offerReactive.save(newOfferEnt))
                    .map(OfferEntity::toOffer);
        }
    }

    public Mono<Void> assertReferenceNotExist(String reference) {
        return this.offerReactive.findByReference(reference)
                .flatMap(offerEntity -> Mono.error(
                        new ConflictException("Offer Reference already exists : " + reference)
                ));
    }
}
