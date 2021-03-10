package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.ARTICLE_FAMILY_CRUD;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyCrudResource.REFERENCE;
import static org.junit.jupiter.api.Assertions.*;

@RestTestConfig
public class ArticleFamilyCrudResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testGivenReferenceWhenIsArticleThenReturnArticle(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY_CRUD + REFERENCE, "zz-falda-T2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(articleFamilyCruds -> {
                    assertEquals(articleFamilyCruds.getReference(), "zz-falda-T2");
                    assertEquals(articleFamilyCruds.getDescription(), "Zarzuela - Falda T2");
                    assertEquals(articleFamilyCruds.getTreeType(), TreeType.ARTICLE);
                    assertNotNull(articleFamilyCruds.getArticleFamilyCrudList());
                    assertTrue(articleFamilyCruds.getArticleFamilyCrudList().isEmpty());
                });
    }

    @Test
    void testGivenReferenceWhenIsSizeThenReturnArticleFamilyTree(){
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY_CRUD + REFERENCE, "Zz Falda")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleFamilyCrud.class)
                .value(articleFamilyCruds -> {
                    assertEquals(articleFamilyCruds.getReference(), "Zz Falda");
                    assertEquals(articleFamilyCruds.getTreeType(), TreeType.SIZES);
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().size(),2);
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().get(0).getReference(),"zz-falda-T2");
                    assertEquals(articleFamilyCruds.getArticleFamilyCrudList().get(1).getReference(),"zz-falda-T4");

                });
    }
}
