package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ArticleBarcodeWithParentReferenceDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
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
                    assertEquals(TreeType.SIZES, articleFamilyCruds.getTreeType());
                    assertEquals(2, articleFamilyCruds.getArticleFamilyCrudList().size());
                    assertEquals("zz-falda-T2", articleFamilyCruds.getArticleFamilyCrudList().get(0).getReference());
                    assertEquals("8400000000017", articleFamilyCruds.getArticleFamilyCrudList().get(0).getBarcode());
                    assertEquals("zz-falda-T4", articleFamilyCruds.getArticleFamilyCrudList().get(1).getReference());

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
    void testGivenNewArticleFamilyWhenCreateThenIsOkAndReturnArticleFamily() {
        AtomicReference<String> idFamilyArticle = new AtomicReference<>("");
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
                    assertEquals("Zz Falda", returnArticleFamilyCrud.getReference());
                    idFamilyArticle.set(Objects.requireNonNull(returnArticleFamilyCrud.getArticleFamilyCrudList()
                            .stream()
                            .filter(element -> element.getReference().equals("test-ref"))
                            .findAny()
                            .orElse(null))
                            .getId());
                });
        setDescriptionOfArticleFamily(idFamilyArticle, articleFamilyCrud);
        deleteArticleFamilyById(idFamilyArticle);
    }

    private void deleteArticleFamilyById(AtomicReference<String> idFamilyArticle) {
        this.restClientTestService.loginAdmin(webTestClient)
                .delete()
                .uri(ARTICLE_FAMILY_CRUD + ID, idFamilyArticle.get())
                .exchange()
                .expectStatus().isOk();
    }

    private void setDescriptionOfArticleFamily(AtomicReference<String> idFamilyArticle, ArticleFamilyCrud articleFamilyCrud) {
        articleFamilyCrud.setId(idFamilyArticle.get());
        articleFamilyCrud.setDescription("New Description");
        this.restClientTestService.loginAdmin(webTestClient)
                .put()
                .uri(ARTICLE_FAMILY_CRUD)
                .body(Mono.just(articleFamilyCrud), ArticleFamilyCrud.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(Assertions::assertNotNull)
                .value(returnArticleFamilyCrud -> {
                    assertEquals("test-ref", returnArticleFamilyCrud.getReference());
                    assertEquals("New Description", returnArticleFamilyCrud.getDescription());
                });
    }

    @Test
    void testGivenSingleArticleWhenAddThenIsOkAndReturnArticleFamily() {
        AtomicReference<String> idSingleArticle = new AtomicReference<>("");
        ArticleBarcodeWithParentReferenceDto articleBarcodeWithParentReferenceDto = ArticleBarcodeWithParentReferenceDto.builder().barcode("8400000000079").parentReference("Zz Falda").build();
        this.restClientTestService.loginAdmin(webTestClient)
                .post()
                .uri(ARTICLE_FAMILY_CRUD + SINGLE)
                .body(Mono.just(articleBarcodeWithParentReferenceDto), ArticleFamilyCrud.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(Assertions::assertNotNull)
                .value(returnArticleFamilyCrud -> {
                    assertEquals("Zz Falda", returnArticleFamilyCrud.getReference());
                    idSingleArticle.set(Objects.requireNonNull(returnArticleFamilyCrud.getArticleFamilyCrudList()
                            .stream()
                            .filter(element -> element.getBarcode().equals("8400000000079"))
                            .findAny()
                            .orElse(null))
                            .getId());

                });

        deleteArticleFamilyById(idSingleArticle);
    }
}
