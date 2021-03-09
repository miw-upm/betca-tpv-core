package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class ArticleFamilyCrudPersistenceMongodbIT {

    @Autowired
    private FamilyArticleCrudPersistenceMongodb familyArticleCrudPersistenceMongodb;

    @Test
    void readSingleArticleByReference(){
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.readByReference("zz-falda-T2"))
                .expectNextMatches(articleFamilyDto -> {
                    assertEquals("zz-falda-T2",articleFamilyDto.getReference());
                    assertEquals("Zarzuela - Falda T2",articleFamilyDto.getDescription());
                    assertNotNull(articleFamilyDto.getArticleFamilyCrudList());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void readComposeArticleByReference(){
        StepVerifier
                .create(this.familyArticleCrudPersistenceMongodb.readByReference("Zz Falda"))
                .expectNextMatches(articleFamilyDto -> {
                    assertEquals(2,articleFamilyDto.getArticleFamilyCrudList().size());
                    assertEquals("zz-falda-T2",articleFamilyDto.getArticleFamilyCrudList().get(0).getReference());
                    assertEquals("zz-falda-T4",articleFamilyDto.getArticleFamilyCrudList().get(1).getReference());
                    return true;
                })
                .expectComplete()
                .verify();
    }

}
