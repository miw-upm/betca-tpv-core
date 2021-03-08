package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OfferCreationEditionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class OfferPersistenceMongodbIT {

    @Autowired
    private OfferPersistenceMongodb offerPersistenceMongodb;

    @Test
    void testCreate() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder().description("test create persistence")
                                .expiryDate(LocalDate.of(2021, Month.MARCH, 31))
                                .discount(new BigDecimal("75"))
                                .articleBarcodes(new String[]{"8400000000031", "8400000000024", "8400000000017"})
                                .build()))
                .expectNextMatches(offer -> {
                    assertNull(offer.getReference());
                    assertEquals("test create persistence", offer.getDescription());
                    assertEquals(new BigDecimal("75"), offer.getDiscount());
                    assertNotNull(offer.getArticleBarcodes());
                    return true;
                })
                .expectComplete()
                .verify();
    }


/*
OfferCreationEditionDto newOffer = new OfferCreationEditionDto(null, "new offer",
            LocalDate.of(2021, 9, 15), new BigDecimal("75"),
            new String[]{"8400000000031", "8400000000024", "8400000000017"});
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
    }*/
}
