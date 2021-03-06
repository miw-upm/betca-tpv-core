package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class OfferPersistenceMongodbIT {

    @Autowired
    private OfferPersistenceMongodb offerPersistenceMongodb;

    @Test
    void testCreate() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder().reference("123").description("create")
                                .expiryDate(LocalDateTime.of(2021, Month.MARCH, 31, 20, 20))
                                .discount(new BigDecimal("50")).articleBarcodeList(List.of("8400000000017", "8400000000031"))
                                .build()))
                .expectNextMatches(offer -> {
                    assertEquals("123", offer.getReference());
                    assertEquals(new BigDecimal("50"), offer.getDiscount());
                    assertTrue(offer.getArticleBarcodeList().contains("8400000000017"));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateExistingReference() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder().reference("cmVmZXJlbmNlb2ZmZXIy").description("error").build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateNotExistingBarcode() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder().reference("123").description("error")
                                .articleBarcodeList(List.of("8400000000017", "hh")).build()))
                .expectError(NotFoundException.class)
                .verify();
    }
}
