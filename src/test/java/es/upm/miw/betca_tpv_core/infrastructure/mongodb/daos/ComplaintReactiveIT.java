package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.Article;
import es.upm.miw.betca_tpv_core.domain.model.Provider;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintState;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ProviderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ComplaintReactiveIT {
    @Autowired
    public ComplaintReactive complaintReactive;
    @Autowired
    public ArticleReactive articleReactive;

    @Test
    void testFindByUserMobileNullSafe_MobileWithComplaintsAssociated(){
        StepVerifier
                .create(this.complaintReactive.findByUserMobileNullSafe("66"))
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja aleatoria"));
                    assertEquals(ComplaintState.OPEN,complaint.getState());
                    return true;
                })
                .expectNextMatches( complaint ->{
                    assertTrue(complaint.getDescription().contains("Queja MIW"));
                    assertTrue(complaint.getReply().contains("Respuesta MIW"));
                    assertEquals(ComplaintState.CLOSED,complaint.getState());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobileNullSafe_MobileWithNoComplaintsAssociated(){
        StepVerifier
                .create(this.complaintReactive.findByUserMobileNullSafe("6"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testFindByUserMobileNullSafe_NullMobile() {
        StepVerifier
                .create(this.complaintReactive.findByUserMobileNullSafe(null))
                .assertNext(complaint -> assertNotNull(complaint.getDescription()))
                .thenCancel()
                .verify();
    }
    @Test
    void testFindByUserMobileAndBarcode_ShouldCompleteWhenNoComplaintExists() {
        StepVerifier
                .create(
                        articleReactive.findByBarcode("8400000000100")
                                .flatMap(article -> complaintReactive.findByUserMobileAndArticle("66", article))
                )
                .expectComplete()
                .verify();
    }
    @Test
    void testFindByUserMobileAndBarcode_ShouldReturnComplaintExists() {
        StepVerifier
                .create(
                        articleReactive.findByBarcode("8400000000017")
                                .flatMap(article -> complaintReactive.findByUserMobileAndArticle("66", article))
                )
                .expectNextMatches(complaint -> complaint != null && complaint.getArticle().getBarcode().equals("8400000000017") && complaint.getUserMobile().equals("66"))
                .expectComplete()
                .verify();
    }
}
