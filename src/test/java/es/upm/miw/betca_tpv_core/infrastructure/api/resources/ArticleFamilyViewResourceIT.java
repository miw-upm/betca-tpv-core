package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyViewResource.ARTICLE_FAMILY;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.ArticleFamilyViewResource.REFERENCE_ID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTestConfig
public class ArticleFamilyViewResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testFindByReferenceThenReturnArticlesFamily() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(ARTICLE_FAMILY+REFERENCE_ID,"undefined")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ArticleFamilyView.class)
                .value(Assertions::assertNotNull)
                .value(articleFamilyViews ->
                        assertEquals("Zz",articleFamilyViews.get(0).getReference()) );
    }
}
