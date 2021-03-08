package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
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
}
