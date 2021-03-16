package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyCrud;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.domain.persistence.ArticleFamilyCrudPersistence;
import es.upm.miw.betca_tpv_core.domain.rest.UserMicroservice;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.AfterTestClass;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@TestConfig
class ArticleFamilyCrudServiceIT {
    @Autowired
    private ArticleFamilyCrudService articleFamilyCrudService;

    @MockBean
    private ArticleFamilyCrudPersistence articleFamilyCrudPersistence;

    @BeforeEach
    public void setup() {
        BDDMockito.when(this.articleFamilyCrudPersistence.readByReference(anyString()))
                .thenReturn(Mono.just(ArticleFamilyCrud.builder()
                        .reference("mockReference")
                        .description("mockDescription")
                        .treeType(TreeType.ARTICLE)
                        .build()));
    }

    @Test
    void testGivenReferenceWhenFindReferenceThenIsCorrect() {

        StepVerifier
                .create(this.articleFamilyCrudService.read("any"))
                .expectNextMatches(articleLeaf -> {
                    assertEquals("mockReference",articleLeaf.getReference());
                    assertEquals("mockDescription",articleLeaf.getDescription());
                    assertEquals(TreeType.ARTICLE,articleLeaf.getTreeType());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testGivenReferenceWhenFindReferenceThenContentsIsEmptyAndNotNull() {
        StepVerifier
                .create(this.articleFamilyCrudService.read("any"))
                .expectNextMatches(articleLeaf -> {
                    assertTrue(articleLeaf.getArticleFamilyCrudList().isEmpty());
                    assertNotNull(articleLeaf.getArticleFamilyCrudList());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
