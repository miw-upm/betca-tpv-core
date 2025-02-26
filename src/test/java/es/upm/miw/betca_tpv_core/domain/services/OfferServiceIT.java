package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class OfferServiceIT {

    @Autowired
    private OfferService offerService;

    @Test
    void testCreateOffer() {

        Offer offer = Offer.builder()
                .reference("ref1")
                .description("offer1")
                .discount(BigDecimal.ONE)
                .articleList(Collections.emptyList())
                .build();

        StepVerifier.create(offerService.create(offer)).expectNextMatches(returnOffer -> {
            assertNotNull(returnOffer);
            assertEquals(returnOffer.getReference(), offer.getReference());
            assertEquals(returnOffer.getDescription(), offer.getDescription());
            assertEquals(returnOffer.getReference(), offer.getReference());
            return true;
        }).verifyComplete();
    }

    @Test
    void testCreateOfferDateError() {

        Offer offer = Offer.builder()
                .reference("ref1")
                .description("offer1")
                .discount(BigDecimal.ONE)
                .articleList(Collections.emptyList())
                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                .expiryDate(LocalDateTime.of(2018, Month.JANUARY, 12, 10, 10))
                .build();

        StepVerifier.create(offerService.create(offer))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testCreateArticleException() {

        Article article = Article.builder().barcode("kk").description("error").retailPrice(TEN).build();
        Offer offer = Offer.builder()
                .reference("ref22")
                .description("offer1")
                .discount(BigDecimal.ONE)
                .articleList(Collections.emptyList())
                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                .expiryDate(LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10))
                .articleList(List.of(article))
                .build();

        StepVerifier.create(this.offerService.create(offer))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.offerService.read("SAVE10LKAUJNRN"))
                .expectNextMatches(offer -> {
                    assertEquals("SAVE10LKAUJNRN", offer.getReference());
                    assertEquals("Offer code 10% discount", offer.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadByReferenceNotFRound() {
        StepVerifier
                .create(this.offerService.read("kk"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        StepVerifier
                .create(this.offerService.findByReferenceAndDescriptionNullSafe(
                        "SAVE10LKAUJNRN", null))
                .expectNextMatches(offer -> {
                    assertEquals("SAVE10LKAUJNRN", offer.getReference());
                    assertEquals("Offer code 10% discount", offer.getDescription());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testUpdate() {
        Article article = Article.builder().barcode("1").description("OK").retailPrice(TEN).build();
        Offer offer = Offer.builder()
                .reference("ref23")
                .description("OK")
                .discount(TEN)
                .creationDate(LocalDateTime.of(2019, Month.JANUARY, 12, 10, 10))
                .expiryDate(LocalDateTime.of(2020, Month.JANUARY, 12, 10, 10))
                .articleList(List.of(article))
                .build();
        StepVerifier
                .create(this.offerService.update("SAVE10LKAUJNRN", offer))
                .expectNextMatches(returnOffer -> {
                    assertEquals("ref23", returnOffer.getReference());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testPdf() {
        StepVerifier
                .create(this.offerService.readPdf("SAVE5IAKMWKIAO"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
