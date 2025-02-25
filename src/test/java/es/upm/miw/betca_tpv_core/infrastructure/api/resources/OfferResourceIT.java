package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OfferResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class OfferResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Offer offer = Offer.builder().reference("to11123213").description("td1").discount(BigDecimal.ONE).articleList(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertEquals("to11123213", returnOffer.getReference());
                    assertEquals("td1", returnOffer.getDescription());
                    assertEquals(BigDecimal.ONE, returnOffer.getDiscount());
                });
    }

    @Test
    void testCreateNotFoundArticleException() {
        Article article = Article.builder().barcode("not_found").description("tad1").retailPrice(BigDecimal.ONE)
                .providerCompany(null).tax(null).build();
        Offer offer = Offer.builder().reference("to11").description("td1").discount(BigDecimal.ONE).articleList(List.of(article)).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(OFFERS)
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByReferenceAndDescriptionNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OFFERS + SEARCH)
                        .queryParam("reference", "polo")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Offer.class)
                .value(Assertions::assertNotNull)
                .value(offers -> assertTrue(offers
                        .stream().allMatch(offer -> offer.getDescription().toLowerCase().contains("polo"))));
    }

    @Test
    void testReadByReference() {
        Offer offer = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE_ID, "to1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertEquals("to1", returnOffer.getReference());
                    assertEquals("td1", returnOffer.getDescription());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(offer);
    }

    @Test
    void testReadByReferenceNotFoundException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadByReferenceAndUpdate() {
        Offer offer = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(OFFERS + REFERENCE_ID, "to1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnOffer -> {
                    assertEquals("to1", returnOffer.getReference());
                    assertEquals("td1", returnOffer.getDescription());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(offer);
        offer.setReference("other");
        offer = this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(OFFERS + REFERENCE_ID, "to1")
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Offer.class)
                .value(Assertions::assertNotNull)
                .value(returnoffer -> assertEquals("other", returnoffer.getReference()))
                .returnResult()
                .getResponseBody();
        assertNotNull(offer);
        offer.setReference("other2");
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(OFFERS + REFERENCE_ID, "other")
                .body(Mono.just(offer), Offer.class)
                .exchange()
                .expectStatus().isOk();
    }
}
