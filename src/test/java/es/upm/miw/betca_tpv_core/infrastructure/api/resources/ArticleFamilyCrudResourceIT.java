package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.Offer;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.ARTICLE_FAMILY_CRUD;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.REFERENCE;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.OfferResource.OFFERS;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class ArticleFamilyCrudResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testGivenReferenceWhenIsArticleThenReturnArticle() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY_CRUD + REFERENCE, "zz-falda-T2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(articleFamilyCrud -> {
                    assertEquals(articleFamilyCrud.getReference(), "zz-falda-T2");
                    assertEquals(articleFamilyCrud.getDescription(), "Zarzuela - Falda T2");
                    assertEquals(articleFamilyCrud.getTreeType(), TreeType.ARTICLE);
                    assertNotNull(articleFamilyCrud.getArticleFamilyCrudList());
                    assertTrue(articleFamilyCrud.getArticleFamilyCrudList().isEmpty());
                });
    }

    @Test
    void testGivenReferenceWhenIsSizeThenReturnArticleFamilyTree() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY_CRUD + REFERENCE, "Zz Falda")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(articleFamilyCruds -> {
                    assertEquals(articleFamilyCruds.getReference(), "Zz Falda");
                    assertEquals(articleFamilyCruds.getTreeType(), TreeType.SIZES);
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().size(), 2);
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().get(0).getReference(), "zz-falda-T2");
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().get(1).getReference(), "zz-falda-T4");

                });
    }
    
    @Test
    @Order(1)
    void testGivenNewArticleFamilyWhenCreateThenIsOkAndReturnArticleFamily() {
        ArticleFamilyCrud articleFamilyCrud = ArticleFamilyCrud.builder().reference("test-ref").parentReference("Zz Falda").treeType(TreeType.SIZES).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ARTICLE_FAMILY_CRUD)
                .body(Mono.just(articleFamilyCrud), ArticleFamilyCrud.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(Assertions::assertNotNull)
                .value(returnArticleFamilyCrud -> {
                    assertEquals("test-ref", articleFamilyCrud.getReference());
                });
    }

    @Test
    @Order(2)
    void testGivenReferenceWhenDeleteThenIsOk() {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(ARTICLE_FAMILY_CRUD + REFERENCE, "test-ref")
                .exchange()
                .expectStatus().isOk();
    }
}
