package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OfferResource.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
class OfferResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OFFERS + SEARCH_OFFER)
                        .queryParam("reference", "b2Z")
                        .queryParam("description", "2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Offer.class)
                .value(offers -> assertTrue(offers
                        .stream().anyMatch(offer -> offer.getDescription().toLowerCase().contains("this is offer 2"))));
    }

    @Test
    void testCreate() {
        Offer newOffer = new Offer(null, "new offer", null,
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"8400000000031", "8400000000024", "8400000000017"});
        Offer dbOffer = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertNotNull(returnOffer.getReference());
                    assertEquals("new offer", returnOffer.getDescription());
                    assertNotNull(returnOffer.getExpiryDate());
                    assertEquals(new BigDecimal("75"), returnOffer.getDiscount());
                    assertNotNull(returnOffer.getArticleBarcodes());
                }).returnResult().getResponseBody();
        assertNotNull(dbOffer);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT_PDF, dbOffer.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateNotFoundBarcodeException() {
        Offer newOffer = new Offer(null, "article not found", null,
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"kk", "8400000000024", "8400000000017"});
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), Offer.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateUnauthorizedException() {
        Offer newOffer = new Offer(null, "unauthorized exception", null,
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"8400000000031", "8400000000024", "8400000000017"});
        webTestClient
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), Offer.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testReadReference() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE, "cmVmZXJlbmNlb2ZmZXIx")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(offer -> {
                    assertEquals("cmVmZXJlbmNlb2ZmZXIx", offer.getReference());
                    assertEquals("this is offer 1", offer.getDescription());
                    assertEquals(3, offer.getArticleBarcodes().length);
                    assertEquals(new BigDecimal("10"), offer.getDiscount());
                });
    }

    @Test
    void testReadNotFoundReferenceException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE, "not-a-reference")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadByReferenceAndUpdate() {
        Offer updatedOffer = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE, "cmVmZXJlbmNlb2ZmZXIx")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(offer -> {
                    assertEquals("cmVmZXJlbmNlb2ZmZXIx", offer.getReference());
                    assertEquals("this is offer 1", offer.getDescription());
                    assertEquals(3, offer.getArticleBarcodes().length);
                    assertEquals(new BigDecimal("10"), offer.getDiscount());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(updatedOffer);
        String reference = updatedOffer.getReference();
        updatedOffer.setDescription("updated description");
        updatedOffer.setDiscount(new BigDecimal("40"));
        updatedOffer.setArticleBarcodes(new String[]{"8400000000079", "8400000000024"});

        updatedOffer = this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(OFFERS + REFERENCE, "cmVmZXJlbmNlb2ZmZXIx")
                .body(Mono.just(updatedOffer), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertEquals(reference, returnOffer.getReference());
                    assertEquals("updated description", returnOffer.getDescription());
                    assertEquals(new BigDecimal("40"), returnOffer.getDiscount());
                    assertEquals(2, returnOffer.getArticleBarcodes().length);
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(updatedOffer);
    }

    @Test
    void testPrintOffer() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT_PDF, "cmVmZXJlbmNlb2ZmZXIx")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testPrintOfferNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT_PDF, "ref-not-found")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        Offer offerDelete = new Offer("refToDelete", "desToDelete", null,
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"8400000000031", "8400000000024", "8400000000017"});

        Offer dbOffer = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(offerDelete), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertNotNull(dbOffer);
        String refToDelete = dbOffer.getReference();

        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(OFFERS + REFERENCE, refToDelete)
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE, refToDelete)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(OFFERS + REFERENCE, "ref-not-found")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteUnauthorized() {
        webTestClient
                .delete()
                .uri(OFFERS + REFERENCE, "cmVmZXJlbmNlb2ZmZXIx")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
