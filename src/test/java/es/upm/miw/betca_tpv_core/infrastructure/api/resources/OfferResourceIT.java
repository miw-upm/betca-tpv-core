package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleResource.SEARCH;
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
                        .path(OFFERS + SEARCH)
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
        Offer newOffer = Offer.builder().reference("abdcef123456").description("new offer")
                .expiryDate(LocalDateTime.of(2021, Month.MARCH, 31, 20, 20))
                .discount(new BigDecimal("50")).articleBarcodeList(List.of("8400000000017", "8400000000024", "8400000000031"))
                .build();
        Offer dbOffer = this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(newOffer), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    System.out.println(">>>>> Test:: returnOffer:" + returnOffer);
                    assertNotNull(returnOffer.getReference());
                    assertEquals("new offer", returnOffer.getDescription());
                    assertNotNull(returnOffer.getExpiryDate());
                    assertEquals(new BigDecimal("50"), returnOffer.getDiscount());
                    assertNotNull(returnOffer.getArticleBarcodeList());
                }).returnResult().getResponseBody();
        assertNotNull(dbOffer);
    }

    @Test
    void testCreateNotFoundBarcodeException() {
        Offer offer = Offer.builder().reference("123").description("not found offer")
                .expiryDate(LocalDateTime.of(2021, Month.MARCH, 31, 20, 20))
                .discount(new BigDecimal("50")).articleBarcodeList(List.of("kk", "8400000000024", "8400000000031"))
                .build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateUnauthorizedException() {
        Offer offer = Offer.builder().reference("123").description("not found offer")
                .expiryDate(LocalDateTime.of(2021, Month.MARCH, 31, 20, 20))
                .discount(new BigDecimal("50")).articleBarcodeList(List.of("8400000000017", "8400000000024", "8400000000031"))
                .build();
        webTestClient
                .post()
                .uri(OFFERS)
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
