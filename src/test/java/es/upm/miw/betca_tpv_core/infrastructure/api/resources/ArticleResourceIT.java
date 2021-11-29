package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodesDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
class ArticleResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Provider provider = Provider.builder().company("tac1").nif("tan1").phone("666666666").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ProviderResource.PROVIDERS)
                .body(Mono.just(provider), Provider.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Provider.class)
                .value(Assertions::assertNotNull)
                .value(returnProvider -> {
                    assertEquals("tac1", returnProvider.getCompany());
                    assertEquals("tan1", returnProvider.getNif());
                    assertEquals("666666666", returnProvider.getPhone());
                    assertTrue(returnProvider.getActive());
                });

        Article article = Article.builder().barcode("ta99000").description("tad1").retailPrice(BigDecimal.ONE)
                .providerCompany("tac1").tax(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ARTICLES)
                .body(Mono.just(article), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    System.out.println(">>>>> Test:: returnArticle:" + returnArticle);
                    assertEquals("ta99000", returnArticle.getBarcode());
                    assertEquals("tad1", returnArticle.getDescription());
                    assertNotNull(returnArticle.getRegistrationDate());
                    assertEquals("tac1", returnArticle.getProviderCompany());
                });
    }

    @Test
    void testCreateNotFoundProviderException() {
        Article article = Article.builder().barcode("ta99002").description("tad2").retailPrice(BigDecimal.ONE)
                .providerCompany("kk").tax(null).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ARTICLES)
                .body(Mono.just(article), Article.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadByBarcodeAndUpdate() {
        Article article = this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLES + BARCODE_ID, "8400000000031")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> {
                    assertEquals("8400000000031", returnArticle.getBarcode());
                    assertEquals("descrip-a3", returnArticle.getDescription());
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(article);
        article.setReference("other");
        article = this.restClientTestService.loginAdmin(webTestClient)

                .put()
                .uri(ARTICLES + BARCODE_ID, "8400000000031")
                .body(Mono.just(article), Article.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Article.class)
                .value(Assertions::assertNotNull)
                .value(returnArticle -> assertEquals("other", returnArticle.getReference()))
                .returnResult()
                .getResponseBody();
        assertNotNull(article);
        article.setReference("ref-a3");
        this.restClientTestService.loginAdmin(webTestClient)

                .put()
                .uri(ARTICLES + BARCODE_ID, "8400000000031")
                .body(Mono.just(article), Article.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testReadByBarcodeNotFoundException() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLES + BARCODE_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ARTICLES + SEARCH)
                        .queryParam("reference", "polo")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Article.class)
                .value(Assertions::assertNotNull)
                .value(articles -> assertTrue(articles
                        .stream().allMatch(article -> article.getDescription().toLowerCase().contains("polo"))));
    }

    @Test
    void testFindByUnfinished() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLES + UNFINISHED)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Article.class)
                .value(Assertions::assertNotNull)
                .value(articles -> assertTrue(articles.stream()
                        .anyMatch(article -> Objects.isNull(article.getProviderCompany()))));
    }

    @Test
    void testFindByBarcode() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ARTICLES + BARCODE)
                        .queryParam("barcode", "100")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleBarcodesDto.class)
                .value(Assertions::assertNotNull)
                .value(barcodesDto -> assertTrue(barcodesDto.getBarcodes().stream()
                        .allMatch(barcode -> barcode.contains("100"))
                ));
    }
}
