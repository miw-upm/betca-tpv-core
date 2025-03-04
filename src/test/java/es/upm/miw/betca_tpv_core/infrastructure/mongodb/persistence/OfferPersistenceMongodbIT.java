package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class OfferPersistenceMongodbIT {

    @Autowired
    private OfferPersistenceMongodb offerPersistenceMongodb;

    @Test
    void testCreateDateException() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder()
                                .reference("to1")
                                .description("error")
                                .discount(10)
                                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                                .expiryDate(LocalDateTime.of(2018, Month.JANUARY, 12, 10, 10))
                                .build()
                        )
                )
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testCreateArticleException() {
        Article article = Article.builder().barcode("kk").description("error").retailPrice(TEN).build();
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                                Offer.builder()
                                        .reference("ref123")
                                        .description("error")
                                        .discount(10)
                                        .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                                        .expiryDate(LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10))
                                        .articleList(List.of(article))
                                        .build()
                        )
                )
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testCreateArticle() {
        Article article = Article.builder().barcode("1").description("OK").retailPrice(TEN).build();
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                                Offer.builder()
                                        .reference("to11")
                                        .description("OK")
                                        .discount(10)
                                        .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                                        .expiryDate(LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10))
                                        .articleList(List.of(article))
                                        .build()
                        )
                ).expectNextMatches(offer -> {
                    assertEquals("to11", offer.getReference());
                    assertEquals("OK", offer.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.offerPersistenceMongodb.readByReference("R_hRxaoeQuyEDdZpmpboVg"))
                .expectNextMatches(offer -> {
                    assertEquals("R_hRxaoeQuyEDdZpmpboVg", offer.getReference());
                    assertEquals("Offer code 20% discount", offer.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotFRound() {
        StepVerifier
                .create(this.offerPersistenceMongodb.readByReference("kk"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        StepVerifier
                .create(this.offerPersistenceMongodb.findByReferenceAndDescriptionAndDiscountNullSafe(
                        "R_hRxaoeQuyEDdZpmpboVg", null, null))
                .expectNextMatches(offer -> {
                    assertEquals("R_hRxaoeQuyEDdZpmpboVg", offer.getReference());
                    assertEquals("Offer code 20% discount", offer.getDescription());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testUpdate() {
        Article article = Article.builder().barcode("1").description("OK").retailPrice(TEN).build();
        Offer offer = Offer.builder()
                .description("OK")
                .discount(10)
                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                .expiryDate(LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10))
                .articleList(List.of(article))
                .build();
        StepVerifier
                .create(this.offerPersistenceMongodb.update("R_hRxaoeQuyEDdZpmpboVg", offer))
                .expectNextMatches(returnOffer -> {
                    assertNotNull(returnOffer);
                    assertEquals("OK", returnOffer.getDescription());
                    return true;
                })
                .verifyComplete();
    }
}
