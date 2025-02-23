package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.persistence.OfferPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.OfferReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.OfferEntity;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
public class OfferPersistenceMongodb implements OfferPersistence {

    private final OfferReactive offerReactive;
    private final ArticleReactive articleReactive;

    @Autowired
    public OfferPersistenceMongodb(OfferReactive offerReactive, ArticleReactive articleReactive) {
        this.offerReactive = offerReactive;
        this.articleReactive = articleReactive;
    }

    @Override
    public Mono<Offer> create(Offer offer) {
        if (offer.getCreationDate() != null && offer.getExpiryDate() != null &&
                !offer.getCreationDate().isBefore(offer.getExpiryDate())) {
            return Mono.error(new BadRequestException("The creation date must be before the expiry date."));
        }
        return this.assertReferenceNotExist(offer.getReference())
                .thenMany(Flux.fromIterable(offer.getArticleList()))
                .flatMap(article -> this.articleReactive.findByBarcode(article.getBarcode())
                        .switchIfEmpty(Mono.error(
                                new NotFoundException("Non existent article with barcode: " + article.getBarcode())
                        ))
                )
                .collectList()
                .map(articleEntities -> new OfferEntity(offer, articleEntities))
                .flatMap(this.offerReactive::save)
                .map(OfferEntity::toOffer);
    }

    @Override
    public Flux<Offer> findByReferenceAndDescriptionNullSafe(String reference, String description) {
        return this.offerReactive.findByReferenceAndDescriptionNullSafe(reference, description)
                .map(OfferEntity::toOfferWithoutArticles);
    }

    private Mono<Void> assertReferenceNotExist(String reference) {
        return this.offerReactive.findByReference(reference)
                .flatMap(offerEntity -> Mono.error(
                        new ConflictException("Offer reference already exists : " + reference)
                ));
    }
}
