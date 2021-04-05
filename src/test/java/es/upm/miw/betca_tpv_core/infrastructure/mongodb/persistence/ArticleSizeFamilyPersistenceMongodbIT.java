package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.ArticleSizeFamily;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class ArticleSizeFamilyPersistenceMongodbIT {
    @Autowired
    private ArticleSizeFamilyPersistenceMongodb articleSizeFamilyPersistenceMongodb;

    @Test
    void testCreate(){
        StepVerifier
                .create(this.articleSizeFamilyPersistenceMongodb.create(ArticleSizeFamily.builder()
                .description("asdaf").build()))
                .expectNextMatches(articleSizeFamily -> {
                    assertEquals("asdaf",articleSizeFamily.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
