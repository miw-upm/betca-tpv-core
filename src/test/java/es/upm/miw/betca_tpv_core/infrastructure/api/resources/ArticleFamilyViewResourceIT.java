package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyViewResource.ARTICLE_FAMILY;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyViewResource.REFERENCE_ID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTestConfig
class ArticleFamilyViewResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testGivenExistentReferenceWhenGetArticleFamilyViewThenReturn() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY+REFERENCE_ID,"undefined")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArticleFamilyView.class)
                .value(Assertions::assertNotNull)
                .value(articleFamilyViews ->{
                        this.verifyContainsReferenceInList(articleFamilyViews,"Zz");
                        this.verifyContainsDescriptionInList(articleFamilyViews,"Zarzuela");
                        this.verifyContainsReferenceInList(articleFamilyViews,"varios");
                        this.verifyContainsReferenceInList(articleFamilyViews,"ref-a3");
                        this.verifyContainsDescriptionInList(articleFamilyViews,"descrip-a3");
                });
    }

    @Test
    void testFindByNonExistentReferenceThenNotFound() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY+REFERENCE_ID,"ExampleNonExistentReference")
                .exchange()
                .expectStatus().isNotFound();
    }

    private void verifyContainsReferenceInList(List<ArticleFamilyView> articleFamilyViewList, String reference){
        assertNotNull(articleFamilyViewList
                .stream()
                .filter(articleFamilyView -> articleFamilyView.getReference().equals(reference))
                .findAny()
                .orElse(null));
    }

    private void verifyContainsDescriptionInList(List<ArticleFamilyView> articleFamilyViewList,String description){
        assertNotNull(articleFamilyViewList
                .stream()
                .filter(articleFamilyView -> articleFamilyView.getDescription().equals(description))
                .findAny()
                .orElse(null));
    }
}
