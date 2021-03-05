package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OfferReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OfferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

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
}
