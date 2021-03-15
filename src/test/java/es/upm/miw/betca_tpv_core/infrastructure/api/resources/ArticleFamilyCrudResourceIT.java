package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.ARTICLE_FAMILY_CRUD;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.REFERENCE;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticleFamilyCrudResourceIT {

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
                    assertEquals("zz-falda-T2", articleFamilyCrud.getReference());
                    assertEquals("Zarzuela - Falda T2", articleFamilyCrud.getDescription());
                    assertEquals(TreeType.ARTICLE, articleFamilyCrud.getTreeType());
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
                    assertEquals("Zz Falda", articleFamilyCruds.getReference());
                    assertEquals(TreeType.SIZES,articleFamilyCruds.getTreeType());
                    assertEquals(2,articleFamilyCruds.getArticleFamilyCrudList().size());
                    assertEquals("zz-falda-T2",articleFamilyCruds.getArticleFamilyCrudList().get(0).getReference());
                    assertEquals("zz-falda-T4",articleFamilyCruds.getArticleFamilyCrudList().get(1).getReference());

                });
    }

    @Test
    void testGivenNewArticleFamilyWithExistentReferenceWhenPostThenConflictException() {
        ArticleFamilyCrud articleFamilyCrud = ArticleFamilyCrud.builder().reference("Zz").parentReference("Zz Falda").treeType(TreeType.SIZES).build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ARTICLE_FAMILY_CRUD)
                .body(Mono.just(articleFamilyCrud), ArticleFamilyCrud.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ConflictException.class);
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
                .value(returnArticleFamilyCrud -> assertEquals("test-ref", articleFamilyCrud.getReference()));
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
