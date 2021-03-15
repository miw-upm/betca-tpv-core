package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ArticleFamilyView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class ArticleFamilyViewPersistenceMongodbIT {

    @Autowired
    private ArticleFamilyViewPersistenceMongodb articleFamilyViewPersistenceMongodb;

    @Test
    void testGiveUndefinedReferenceWhenReadByReferenceThenReturn() {
        StepVerifier
                .create(this.articleFamilyViewPersistenceMongodb.readByReference("undefined"))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(articleFamilyViews -> {
                            List<ArticleFamilyView> articleFamilyViewList = new ArrayList<>(articleFamilyViews);
                            this.verifyContainsReferenceInList(articleFamilyViewList,"Zz");
                            this.verifyContainsReferenceInList(articleFamilyViewList,"varios");
                            this.verifyContainsReferenceInList(articleFamilyViewList,"ref-a3");
                            this.verifyContainsDescriptionInList(articleFamilyViewList,"descrip-a3");
                            assertNotNull(articleFamilyViewList
                                    .stream()
                                    .filter(x -> x.getBarcode() != null)
                                    .filter(articleFamilyView -> articleFamilyView.getBarcode().equals("8400000000031"))
                                    .findAny()
                                    .orElse(null));
                            return true;
                        }
                )
                .verifyComplete();
    }

    @Test
    void testGivenExistentReferenceWhenReadByReferenceThenReturn() {
        StepVerifier
                .create(this.articleFamilyViewPersistenceMongodb.readByReference("Zz"))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(articleFamilyViews -> {
                            List<ArticleFamilyView> articleFamilyViewList = new ArrayList<>(articleFamilyViews);
                            this.verifyContainsReferenceInList(articleFamilyViewList,"Zz Falda");
                            this.verifyContainsDescriptionInList(articleFamilyViewList, "Zarzuela - Falda");
                            this.verifyContainsReferenceInList(articleFamilyViewList,"Zz Polo");
                            this.verifyContainsDescriptionInList(articleFamilyViewList,"Zarzuela - Polo");
                            return true;
                        }
                )
                .verifyComplete();
    }

    @Test
    void testGivenNonExistentReferenceWhenReadByReferenceThenNotFoundException() {
        StepVerifier
                .create(this.articleFamilyViewPersistenceMongodb.readByReference("NonExistentReference"))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Not Found Exception. Non existent article family reference: NonExistentReference"))
                .verify();
    }

    private void verifyContainsReferenceInList(List<ArticleFamilyView> articleFamilyViewList,String reference){
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
