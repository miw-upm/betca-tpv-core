package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.TreeType;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous.ArticlesTreeDao;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticlesTreeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticlesTreeReactiveIT {

    @Autowired
    private ArticlesTreeDao articlesTreeDao;

    @Autowired
    private ArticlesTreeReactive articlesTreeReactive;

    @Test
    void testFindAll() {
        List<ArticlesTreeEntity> list = this.articlesTreeDao.findByReference("root");
        assertFalse(list.isEmpty());
    }

    @Test
    void testGivenReferenceWhenFindReferenceThenIsCorrect() {
        StepVerifier
                .create(this.articlesTreeReactive.findByReference("zz-falda-T2"))
                .expectNextMatches(articleLeaf -> {
                    assertEquals("zz-falda-T2", articleLeaf.getReference());
                    assertEquals("Zarzuela - Falda T2", articleLeaf.getDescription());
                    assertEquals(TreeType.ARTICLE, articleLeaf.getTreeType());

                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testGivenReferenceWhenFindReferenceThenContentsIsEmptyAndNotNull() {
        StepVerifier
                .create(this.articlesTreeReactive.findByReference("zz-falda-T2"))
                .expectNextMatches(articleLeaf -> {
                    assertTrue(articleLeaf.contents().isEmpty());
                    assertNotNull(articleLeaf.contents());
                    return true;
                })
                .thenCancel()
                .verify();
    }


}
