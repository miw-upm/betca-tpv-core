package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

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

    @Test
    void testCreateExistingReference() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder()
                                .reference("cmVmZXJlbmNlb2ZmZXIy").description("reference already exists")
                                .expiryDate(LocalDate.of(2021, Month.MARCH, 31))
                                .discount(new BigDecimal("75"))
                                .articleBarcodes(new String[]{"8400000000031", "8400000000024", "8400000000017"})
                                .build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateNotExistingBarcode() {
        StepVerifier
                .create(this.offerPersistenceMongodb.create(
                        Offer.builder()
                                .reference("cmVmZXJlbmNlb2ZmZXIy").description("barcode does not exist")
                                .expiryDate(LocalDate.of(2021, Month.MARCH, 31))
                                .discount(new BigDecimal("75"))
                                .articleBarcodes(new String[]{"kk", "8400000000024", "8400000000017"})
                                .build()))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.offerPersistenceMongodb.readByReference("cmVmZXJlbmNlb2ZmZXIy"))
                .expectNextMatches(offer -> {
                    assertEquals("cmVmZXJlbmNlb2ZmZXIy", offer.getReference());
                    assertEquals("this is offer 2", offer.getDescription());
                    assertNotNull(offer.getArticleBarcodes());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
