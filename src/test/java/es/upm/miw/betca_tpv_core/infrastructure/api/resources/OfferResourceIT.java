package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.OfferCreationEditionDto;
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
public class OfferResourceIT {

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
        OfferCreationEditionDto newOffer = new OfferCreationEditionDto(null, "new offer",
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"8400000000031", "8400000000024", "8400000000017"});
        Offer dbOffer = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), OfferCreationEditionDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    System.out.println(">>>>> Test:: returnOffer:" + returnOffer);
                    assertNotNull(returnOffer.getReference());
                    assertEquals("new offer", returnOffer.getDescription());
                    assertNotNull(returnOffer.getExpiryDate());
                    assertEquals(new BigDecimal("75"), returnOffer.getDiscount());
                    assertNotNull(returnOffer.getArticleBarcodes());
                }).returnResult().getResponseBody();
        assertNotNull(dbOffer);
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT, dbOffer.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateNotFoundBarcodeException() {
        OfferCreationEditionDto newOffer = new OfferCreationEditionDto(null, "article not found",
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"kk", "8400000000024", "8400000000017"});
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), OfferCreationEditionDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateUnauthorizedException() {
        OfferCreationEditionDto newOffer = new OfferCreationEditionDto(null, "article not found",
                LocalDate.of(2021, 9, 15), new BigDecimal("75"),
                new String[]{"8400000000031", "8400000000024", "8400000000017"});
        webTestClient
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), OfferCreationEditionDto.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testCreateResource() {
        OfferCreationEditionDto newOffer = new OfferCreationEditionDto(null, "222",
                LocalDate.of(2021, 9, 15), new BigDecimal("66"),
                new String[]{"8400000000031", "8400000000024"});

        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), OfferCreationEditionDto.class)
                .exchange()
                .expectStatus().isOk();
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
                    assertEquals(offer.getArticleBarcodes().length, 3);
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
                    assertEquals(offer.getArticleBarcodes().length, 3);
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
                .body(Mono.just(updatedOffer), OfferCreationEditionDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertEquals(reference, returnOffer.getReference());
                    assertEquals("updated description", returnOffer.getDescription());
                    assertEquals(new BigDecimal("40"), returnOffer.getDiscount());
                    assertEquals(returnOffer.getArticleBarcodes().length, 2);
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(updatedOffer);
    }

    @Test
    void testPrintOffer() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT, "cmVmZXJlbmNlb2ZmZXIx")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testPrintOfferNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE + PRINT, "ref-not-found")
                .exchange()
                .expectStatus().isNotFound();
    }
}
