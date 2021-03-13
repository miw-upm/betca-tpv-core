package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class ArticleFamilyViewPersistenceMongodbIT {

    @Autowired
    private ArticleFamilyViewPersistenceMongodb articleFamilyViewPersistenceMongodb;

    @Test
    void testReadByReference() {
        StepVerifier
                .create(this.articleFamilyViewPersistenceMongodb.readByReference("undefined"))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(articleFamilyViews -> {
                            List<ArticleFamilyView> articleFamilyViewList = new ArrayList<>(articleFamilyViews);
                            assertEquals("Zz",articleFamilyViewList.get(0).getReference());
                            assertEquals("varios",articleFamilyViewList.get(1).getReference());
                            return true;
                        }
                )
                .verifyComplete();
    }

}
